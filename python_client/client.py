import grpc
import stockmarket_pb2
import stockmarket_pb2_grpc
from time import sleep


def user_input_handler():
    indexes = set()
    while True:
        print("Index name:")
        index = input()
        if index == "#EXIT":
            break
        print("Lower bound: ")
        lower_bound = float(input())
        print("Upper bound: ")
        upper_bound = float(input())
        if (lower_bound > upper_bound) and (lower_bound > 0 and upper_bound > 0):
            print("Invalid bounds, index want be remembered!")
        indexes.add((index, lower_bound, upper_bound))
    return indexes


# preparation
wait_time = 0.1
channel = grpc.insecure_channel('localhost:50052', options=[('grpc.keepalive_time_ms', 60000)])
stub = stockmarket_pb2_grpc.IndicesServiceStub(channel)
number_to_name = {i: n for i, n in enumerate(stockmarket_pb2.Index.keys())}
available_indexes = {str(i): i for i in stockmarket_pb2.Index.keys()}

print("Select indexes, lower bound and upper bound for its values")
print("Note that if you don't want to use lower/upper bound set it to any negative number")
print("Note that you can subscribe only once")
print("If you have done, type '#EXIT'")
print(available_indexes.keys())

# building model representing user input
indexes = user_input_handler()
indexes = list(filter(lambda x: x[0] in available_indexes.keys(), map(lambda i: (i[0].upper(), i[1], i[2]), indexes)))
mapped_indexes = list(map(lambda i: (available_indexes[i[0]], i[1], i[2]), indexes))

# building request
patterns = []
for p in mapped_indexes:
    pattern = stockmarket_pb2.SingleIndexPattern(index=p[0], lowerBound=p[1], upperBound=p[2])
    patterns.append(pattern)
print(patterns)
request = stockmarket_pb2.SubscribeRequest(indexes=patterns)

while True:
    try:
        res = stub.GetStockMarketIndices(request)
        it = iter(res)
        for el in it:
            wait_time = max(0.1, wait_time / 2.0)
            i = next(it)
            print("Index: {}, {}".format(number_to_name[i.index], i.value))
    except Exception as e:
        print("error occured")
        sleep(wait_time)
        # self-adapting time
        wait_time = min(10.0, wait_time * 2.0)

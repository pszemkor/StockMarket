import grpc
import stockmarket_pb2
import stockmarket_pb2_grpc
import pyfiglet
from time import sleep


def user_input_handler():
    indexes = set()
    while True:
        try:
            index = input("Index name: ")
            lower_bound = float(input("Lower bound: "))
            upper_bound = float(input("Upper bound: "))
            if (lower_bound > upper_bound) and (lower_bound > 0 and upper_bound > 0):
                print("Invalid bounds, index want be remembered!")
            indexes.add((index, lower_bound, upper_bound))
            response = input("Would you like to add another index? (y/n): ")
            if response != 'y':
                break
        except Exception as e:
            print("error: ", str(e))
    return indexes


# preparation
wait_time = 0.1
channel = grpc.insecure_channel('localhost:50052', options=[('grpc.keepalive_time_ms', 60000)])
stub = stockmarket_pb2_grpc.IndicesServiceStub(channel)
number_to_name = {i: n for i, n in enumerate(stockmarket_pb2.Index.keys())}
name_to_number = {name: num for name, num in stockmarket_pb2.Index.items()}
available_indexes = {str(i): i for i in stockmarket_pb2.Index.keys()}

ascii_banner = pyfiglet.figlet_format("Welcome!")
print(ascii_banner)
print(">Select indexes, lower bound and upper bound for its values")
print(">Note that if you don't want to use lower/upper bound set it to any negative number")
print(">Note that you can subscribe for events only once")
print('Available indexes:', ' '.join(available_indexes.keys()))

# building model representing user input
indexes = user_input_handler()
indexes = list(filter(lambda x: x[0] in available_indexes.keys(), map(lambda i: (i[0].upper(), i[1], i[2]), indexes)))

# building request
patterns = []
for p in indexes:
    pattern = stockmarket_pb2.SingleIndexPattern(index=name_to_number[p[0]], lowerBound=p[1], upperBound=p[2])
    patterns.append(pattern)
request = stockmarket_pb2.SubscribeRequest(indexes=patterns)

while True:
    try:
        res = stub.GetStockMarketIndices(request)
        it = iter(res)
        for el in it:
            wait_time = max(0.1, wait_time / 2.0)
            i = next(it)
            print("Index: {}: {}$".format(number_to_name[i.index], i.value))
    except Exception as e:
        print("error occured", str(e))
        sleep(wait_time)
        # self-adapting time
        wait_time = min(15.0, wait_time * 2.0)

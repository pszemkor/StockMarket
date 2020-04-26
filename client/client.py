import grpc
import sys

sys.path.insert(1, '../gen')

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
                continue
            indexes.add((index, lower_bound, upper_bound))
            response = input("Would you like to add another index? (y/n): ")
            if response != 'y':
                break
        except Exception as e:
            print("error: ", str(e))
            sys.stdout.flush()
    return indexes


# preparation
states = {num: name for name, num in stockmarket_pb2.Response.State.items()}
channel = grpc.insecure_channel('localhost:50052', options=[('grpc.keepalive_time_ms', 5 * 60000)])
stub = stockmarket_pb2_grpc.IndicesServiceStub(channel)
wait_time = 0.1
while True:
    try:
        available_indexes = stub.GetAvailableIndexes(stockmarket_pb2.IndexesRequest()).indexes
        break
    except Exception as e:
        print(str(e))
        sys.stdout.flush()
        sleep(1)

# welcome messages
ascii_banner = pyfiglet.figlet_format("Welcome!")
print(ascii_banner)
print(">Select indexes (case insensitive), lower bound and upper bound for its values")
print(">Note that if you don't want to use lower/upper bound set it to any negative number")
print(">Note that you can subscribe for events only once")
print(">When value is above 50000 then status is equal to BULL")
print('Available indexes: ', ' * '.join(available_indexes))
sys.stdout.flush()

# building model representing user input
while True:
    indexes = user_input_handler()
    indexes = list(filter(lambda x: x[0] in available_indexes, map(lambda i: (i[0].upper(), i[1], i[2]), indexes)))
    print("Your choice: ", indexes)
    sys.stdout.flush()
    if len(indexes) != 0:
        break
    else:
        print("Empty subscription")
        sys.stdout.flush()

# building request
patterns = []
for p in indexes:
    pattern = stockmarket_pb2.SingleIndexPattern(index=p[0], lowerBound=p[1], upperBound=p[2])
    patterns.append(pattern)
request = stockmarket_pb2.SubscribeRequest(indexes=patterns)

while True:
    try:
        res = stub.GetStockMarketIndexes(request)
        it = iter(res)
        for el in it:
            wait_time = max(0.1, wait_time / 2.0)
            print("Index: {}: {}$  #>state: {}".format(el.index, el.value, states[el.state]))
            sys.stdout.flush()
    except Exception as e:
        print("error occured", str(e))
        sys.stdout.flush()
        sleep(wait_time)
        # self-adapting time
        wait_time = min(1, wait_time * 2.0)

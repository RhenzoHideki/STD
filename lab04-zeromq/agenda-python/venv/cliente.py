#!/usr/bin/env python3
import zmq, sys
context = zmq.Context()
s = context.socket(zmq.SUB) # create a subscriber socket
HOST = sys.argv[1] if len(sys.argv) > 1 else "localhost"
PORT = sys.argv[2] if len(sys.argv) > 2 else "50009"
p = "tcp://"+ HOST +":"+ PORT # how and where to communicate
s.connect(p) # connect to the server
s.setsockopt(zmq.SUBSCRIBE, b"TIME") # subscribe to TIME messages
for i in range(5): # Five iterations
    time = s.recv() # receive a message
    print(time.decode())
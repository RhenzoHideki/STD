#!/usr/bin/env python3
import zmq
import time
context = zmq.Context()
s = context.socket(zmq.PUB) # create a publisher socket
HOST = "*"
PORT = "50009"
p = "tcp://" + HOST + ":" + PORT # how and where to communicate
s.bind(p) # bind socket to the address
while True:
    time.sleep(5)
    msg = str("TIME " + time.asctime())
    s.send(msg.encode())
#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import sys
import zmq
context = zmq.Context()
HOST = sys.argv[1] if len(sys.argv) > 1 else "localhost"
PORT = sys.argv[2] if len(sys.argv) > 2 else "5555"
servidor = "tcp://"+ HOST +":"+ PORT
# Criando o socket para conectar no servidor
s = context.socket(zmq.REQ)
# Fica bloqueado até conseguir se conectar no servidor
s.connect(servidor)
# Envia a mensagem (sequência de bytes)
s.send(b"Hello")
# Fica bloqueado até receber a resposta do servidor
message = s.recv()
# Imprime a resposta do servidor
print("Recebido {}".format(message))
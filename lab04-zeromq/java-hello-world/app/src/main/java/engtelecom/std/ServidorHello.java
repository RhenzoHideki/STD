package engtelecom.std;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class ServidorHello {
    public static void main(String[] args) {
        try (ZContext context = new ZContext()) {
            // Socket para interagir com os clientes
            ZMQ.Socket socket = context.createSocket(SocketType.REP);
            socket.bind("tcp://*:5555");
            while (!Thread.currentThread().isInterrupted()) {
                System.err.println("Esperando chegar mensagem...");
                // Fica bloqueado até chegar uma mensagem
                byte[] reply = socket.recv(0);
                // Imprime a mensagem recebida
                System.out.println("Recebido: [" + new String(reply, ZMQ.CHARSET) + "]");
                // Envia a resposta
                String response = "Hello, world!";
                socket.send(response.getBytes(ZMQ.CHARSET), 0);
            }
            System.out.println("Fim da comunicação");
        }
    }
}
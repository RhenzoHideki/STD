package engtelecom.std;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class ClienteHello {
    public static void main(String[] args) {
        try (ZContext context = new ZContext()) {
            // Socket para conversar com o servidor
            ZMQ.Socket socket = context.createSocket(SocketType.REQ);
            System.out.println("Conectando no servidor Hello World!");
            // Fica bloqueado até conseguir se conectar no servidor
            socket.connect("tcp://localhost:5555");
            System.out.println("Enviando mensagem ");
            String request = "Hello";
            // Envia a mensagem (sequência de bytes)
            socket.send(request.getBytes(ZMQ.CHARSET), 0);
            // Fica bloqueado até receber a resposta do servidor
            byte[] reply = socket.recv(0);
            System.out.println("Recebido " + new String(reply, ZMQ.CHARSET));
        }
    }
}
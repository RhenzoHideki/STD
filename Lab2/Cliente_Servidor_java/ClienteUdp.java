import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Cliente UDP envia a mensagem ao servidor e imprime a resposta recebida
 * 2014-08-24
 * 
 * @author Emerson Ribeiro de Mello
 */
public class ClienteUdp {
    public static void main(String[] args) throws SocketException, UnknownHostException, IOException {

        DatagramSocket cliente = new DatagramSocket(4321);
        byte[] dadosRecebidos = new byte[1024];
        byte[] dadosEnviados;
        InetAddress endereco = InetAddress.getByName(args[0]);

        String mensagem = "Ola, eu sou o cliente!";

        dadosEnviados = mensagem.getBytes();

        DatagramPacket pacoteEnviado = new DatagramPacket(dadosEnviados, dadosEnviados.length, endereco, 1234);

        cliente.send(pacoteEnviado);

        DatagramPacket pacoteRecebido = new DatagramPacket(dadosRecebidos, dadosRecebidos.length);
        cliente.receive(pacoteRecebido);

        String recebido = new String(pacoteRecebido.getData());
        System.out.println("Recebido: " + recebido);
        cliente.close();

    }
}

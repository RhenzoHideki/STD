import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Servidor UDP imprime a mensagem recebida e depois envia uma resposta ao
 * cliente
 * 
 * 2014-08-24
 *
 * @author Emerson Ribeiro de Mello
 */
public class ServidorUdp {

    public static void main(String[] args) throws SocketException, IOException {

        DatagramSocket servidor = new DatagramSocket(1234);
        byte[] dadosRecebidos = new byte[1024];
        byte[] dadosEnviados;

        System.out.println("Ouvindo na porta 1234... " +
                "\n (pressione CTRL+C para encerrar o processo)\n\n");
        while (true) {
            DatagramPacket pacoteRecebido = new DatagramPacket(dadosRecebidos, dadosRecebidos.length);
            servidor.receive(pacoteRecebido);

            String mensagem = new String(pacoteRecebido.getData());
            System.out.println("Recebido: " + mensagem);
            InetAddress ipOrigem = pacoteRecebido.getAddress();
            int porta = pacoteRecebido.getPort();

            dadosEnviados = "Ola, eu sou o servidor\n".getBytes();
            DatagramPacket pacoteEnviado = new DatagramPacket(dadosEnviados, dadosEnviados.length, ipOrigem, porta);
            servidor.send(pacoteEnviado);

        }

    }

}

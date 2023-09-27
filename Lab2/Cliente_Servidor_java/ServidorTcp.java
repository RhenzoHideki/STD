import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServidorTcp {

    public static void main(String[] args) {

        try (ServerSocket socket = new ServerSocket(1234)) {
            System.out.println("Aguardando por conexoes em: " + socket.getInetAddress() + ":" + socket.getLocalPort() + 
            "\n (pressione CTRL+C para encerrar o processo)\n\n");
            
            try (Socket clientSocket = socket.accept()) {
                // Estabelecendo fluxos de entrada e saída
                BufferedReader entrada = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                DataOutputStream saida = new DataOutputStream(clientSocket.getOutputStream());

                // lendo mensagem enviada pelo cliente
                String mensagem = entrada.readLine();
                System.out.println("Cliente> " + mensagem);

                // enviando mensagem para o cliente
                saida.writeBytes("Oi, eu sou o servidor!\n");

            } catch (Exception e) {
                System.err.println(e.toString());
            }

        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
}
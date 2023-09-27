import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ClienteTcp {

    public static void main(String[] args) throws IOException {
        int servidorPorta = 1234;

        // InetAddress ipDoServidor = InetAddress.getByName("localhost");

        // Pode indicar o IP diretamente como uma String
        String ipDoServidor = args[0];

        try (Socket conexao = new Socket(ipDoServidor, servidorPorta)) {
            System.out.println("Conectado! " + conexao);

            /* Estabelece fluxos de entrada e saida */
            DataOutputStream saida = new DataOutputStream(conexao.getOutputStream());
            BufferedReader entrada = new BufferedReader(new InputStreamReader(conexao.getInputStream()));

            /* Inicia comunicacao */
            String mensagem = "Oi, eu sou o cliente!\n";
            saida.write(mensagem.getBytes());
            saida.flush();

            String recebida = entrada.readLine();
            System.out.println("Servidor> " + recebida);

        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
}

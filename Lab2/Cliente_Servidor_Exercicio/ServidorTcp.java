import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServidorTcp {
    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;

    private static void receiveFile(String fileName)
            throws Exception {
        int bytes = 0;
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);

        long size = dataInputStream.readLong(); // read file size
        byte[] buffer = new byte[4 * 1024];
        while (size > 0
                && (bytes = dataInputStream.read(
                        buffer, 0,
                        (int) Math.min(buffer.length, size))) != -1) {
            // Here we write the file using write method
            fileOutputStream.write(buffer, 0, bytes);
            size -= bytes; // read upto file size
        }
        // Here we received file
        System.out.println("File is Received");
        fileOutputStream.close();
    }

    public static void main(String[] args) {

        // Define a porta 1234
        try (ServerSocket socket = new ServerSocket(1234)) {
            System.out.println("Aguardando por conexoes em: " + socket.getInetAddress() + ":" + socket.getLocalPort() +
                    "\n (pressione CTRL+C para encerrar o processo)\n\n");

            try (Socket clientSocket = socket.accept()) {
                // Estabelecendo fluxos de entrada e saída
                BufferedReader entrada = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                DataOutputStream saida = new DataOutputStream(clientSocket.getOutputStream());

                System.out.println("Connected");
                dataInputStream = new DataInputStream(
                        clientSocket.getInputStream());
                dataOutputStream = new DataOutputStream(
                        clientSocket.getOutputStream());
                receiveFile(args[0]);

            } catch (Exception e) {
                System.err.println(e.toString());
            }

        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
}
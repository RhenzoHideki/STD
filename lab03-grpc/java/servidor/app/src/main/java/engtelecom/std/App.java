package engtelecom.std;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.util.logging.Logger;

public class App {
    // Serviço de log para registrar as mensagens de depuração, informação, erro,
    // etc.
    private static final Logger logger = Logger.getLogger(App.class.getName());

    public static void main(String[] args) throws Exception {
        logger.info("Iniciando o servidor");
        // Padrão de projeto Builder. Veja mais em
        // https://java-design-patterns.com/patterns/builder/
        // Iniciando o servidor com a implementação da AgendaImpl
        Server servidor = ServerBuilder.forPort(50051)
                .addService(new AgendaImpl())
                .build()
                .start();
        // Para finalizar o servidor quando a JVM for finalizada
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("servidor gRPC sendo desligado pois a JVM está sendo desligada");
            servidor.shutdown();
            System.err.println("Servidor parado com sucesso");
        }));
        // Para Finalizar o servidor
        servidor.awaitTermination();
    }
}
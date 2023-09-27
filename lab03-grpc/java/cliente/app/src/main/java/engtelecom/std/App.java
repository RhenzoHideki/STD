package engtelecom.std;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import engtelecom.std.agenda.AgendaGrpc;
import engtelecom.std.agenda.Pessoa;
import engtelecom.std.agenda.Pessoa.NumeroTelefone;
import engtelecom.std.agenda.Pessoa.TipoTelefone;
import io.grpc.ManagedChannelBuilder;

public class App {
    private static final Logger logger = Logger.getLogger(App.class.getName());

    public static void main(String[] args) throws Exception {
        // Por padrão o gRPC sempre será sobre TLS, como não criamos um certificado
        // digital, forçamos aqui nã o usar TLS
        var channel = ManagedChannelBuilder.forTarget("191.36.13.67:50051").usePlaintext().build();
        // Criando uma pessoa usando o padrão de projeto Builder
        var juca = Pessoa.newBuilder().setNome("Rhenzo Hideki")
                .setEmail("Rhenzo.hsk@aluno.ifsc.edu.br")
                .setId(2)
                .addTelefones(
                        NumeroTelefone.newBuilder().setNumero("48 3381-2800").setTipo(TipoTelefone.TRABALHO).build())
                .build();
        logger.info("Adicionando uma pessoa na agenda de contatos no servidor");
        var agendaBlockingStub = AgendaGrpc.newBlockingStub(channel);
        agendaBlockingStub.adicionar(juca);

        logger.info("Pessoa adicionada");
        logger.info("Buscando por uma pessoa na agenda de contatos");
        var resultado = agendaBlockingStub.buscar(juca);
        logger.info("Dados da pessoa retornada pelo servidor: " + resultado);
        logger.info("Finalizando...");
        channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }
}
package engtelecom.std;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import engtelecom.std.agenda.Pessoa;
import engtelecom.std.agenda.Resposta;
import io.grpc.stub.StreamObserver;
import engtelecom.std.agenda.AgendaGrpc.AgendaImplBase;

public class AgendaImpl extends AgendaImplBase {
    // Serviço de log para registrar as mensagens de depuração, informação, erro,
    // etc.
    private static final Logger logger = Logger.getLogger(AgendaImpl.class.getName());
    // Banco de dados para armazenar todos os contatos
    private Map<Integer, Pessoa> agenda;

    public AgendaImpl() {
        this.agenda = new HashMap<>();
    }

    @Override
    public void adicionar(Pessoa request, StreamObserver<Resposta> responseObserver) {
        String mensagem = "id do contato já existe no banco de dados";
        if (!this.agenda.containsKey(request.getId())) {
            this.agenda.put(request.getId(), request);
            mensagem = "Contato com o id " + request.getId() + " foi adicionado com sucesso";
        }
        logger.info(mensagem);
        // Padrão de projeto Builder. Veja mais em
        // https://java-design-patterns.com/patterns/builder/
        Resposta resposta = Resposta.newBuilder().setResultado(mensagem).build();
        responseObserver.onNext(resposta);
        responseObserver.onCompleted();
    }

    @Override
    public void buscar(Pessoa request, StreamObserver<Pessoa> responseObserver) {
        Pessoa resposta = this.agenda.get(request.getId());
        logger.info("Buscar... " + resposta);
        responseObserver.onNext(resposta);
        responseObserver.onCompleted();
    }
}
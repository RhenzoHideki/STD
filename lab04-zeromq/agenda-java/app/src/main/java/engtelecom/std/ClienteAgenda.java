package engtelecom.std;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import com.google.gson.Gson;

import lombok.extern.java.Log;

// Anotação lombok para usar o logger
@Log
public class ClienteAgenda {
    /**
     * Processa a resposta do servidor e converte cada ZFrame para String
     */
    public List<String> processarResposta(ZMsg resposta) {
        List<String> quadros = new ArrayList<>();
        // Obtendo o primeiro frame da mensagem
        Optional<ZFrame> op = resposta.stream().findFirst();
        if (op.isPresent()) {
            log.info("Resposta do servidor: " + op.get().getString(ZMQ.CHARSET));
            // Obtendo os demais frames da mensagem
            resposta.stream().skip(1).forEach(frame -> {
                quadros.add(frame.getString(ZMQ.CHARSET));
            });
        }
        return quadros;
    }

    /**
     * Para enviar pedidos aos servidor
     */
    public void enviarMensagens() {
        // variáveis auxiliares
        var gson = new Gson();
        List<String> respostaDoServidor;
        // Pessoas que serão adicionadas na agenda do servidor
        var juca = new Pessoa(1, "Juca", "juca@email.com");
        juca.adicionarTelefone("casa", "48 3381-2800");
        juca.adicionarTelefone("cel", "48 99876-4321");
        var ana = new Pessoa(2, "Ana", "ana@example.org");
        ana.adicionarTelefone("celular", "48 99999-4421");
        ana.adicionarTelefone("comercial", "48 4444-4400");
        try (ZContext context = new ZContext()) {
            log.info("Conectando no servidor");
            ZMQ.Socket socket = context.createSocket(SocketType.REQ);
            socket.connect("tcp://localhost:5555");
            System.err.println("====================================");
            System.err.println("Adicionando 2 contatos");
            var mensagem = new ZMsg();
            var operacao = new ZFrame("adicionar");
            mensagem.add(operacao);
            mensagem.add(gson.toJson(juca));
            mensagem.add(gson.toJson(ana));
            mensagem.send(socket);
            respostaDoServidor = this.processarResposta(ZMsg.recvMsg(socket));
            respostaDoServidor.forEach(System.out::println);
            System.err.println("====================================");
            System.err.println("====================================");
            System.err.println("Buscando pelo contato com id 1");
            mensagem = new ZMsg();
            operacao = new ZFrame("buscar");
            mensagem.add(operacao);
            mensagem.add(Integer.toString(1));
            mensagem.send(socket);
            respostaDoServidor = this.processarResposta(ZMsg.recvMsg(socket));
            respostaDoServidor.forEach(quadro -> {
                Pessoa p = gson.fromJson(quadro, Pessoa.class);
                System.err.println("Pessoa retornada: " + p);
            });
            System.err.println("====================================");
        }
    }

    public static void main(String[] args) {
        ClienteAgenda app = new ClienteAgenda();
        app.enviarMensagens();
    }
}
package engtelecom.std;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import com.google.gson.Gson;

import lombok.extern.java.Log;

/**
 * - O cliente sempre envia uma mensagem (ZMsg)
 * - Cada mensagem (ZMsg) sempre terá no mínimo um frame (ZFrame) e no máximo N
 * frames
 * - O primeiro frame (ZFrame) de cada mensagem sempre indicará qual operação
 * (String) o cliente deseja
 * executar
 * - Os demais frames de cada mensagem estão relacionados com a lógica de cada
 * operação do servidor
 * - O servidor oferece duas operações: "adicionar" e "buscar"
 * - Na operação "adicionar", cada frame enviado pelo cliente deverá conter uma
 * Pessoa representada em JSON
 * - O servidor retornará uma mensagem, cujo primeiro frame terá a String
 * "adicionado" e N frames
 * adicionais, cada um destes contendo uma String para indicar se a Pessoa foi
 * adicionada com sucesso ou nã
 * o
 * - Na operação "buscar", haverá um único frame que irá conter o id
 * (representado em String) da Pessoa que
 * se deseja buscar
 * - Se existir uma Pessoa com o id informado, então o servidor retornará uma
 * mensagem tendo a string "
 * encontrada" no primeiro
 * frame e a Pessoa representada em JSON no segundo frame.
 * - Se não existir, então retorna a string "nao encontrada"
 */
@Log // Anotação lombok para usar o logger
public class ServidorAgenda {
    public static void main(String[] args) {
        // Para representar o banco de dados - a agenda de contatos
        Map<Integer, Pessoa> agenda = new HashMap<>();
        // Para ajudar na conversão de objetos Java em JSON
        Gson gson = new Gson();
        try (ZContext context = new ZContext()) {
            ZMQ.Socket socket = context.createSocket(SocketType.REP);
            socket.bind("tcp://*:5555");
            log.info("Servidor pronto! Esperando por conexões");
            while (!Thread.currentThread().isInterrupted()) {
                // Fica bloqueado até chegar uma mensagem
                ZMsg mensagem = ZMsg.recvMsg(socket);
                ZMsg msgResposta = new ZMsg();
                System.err.println("====================================");
                System.err.println("Chegou novo pedido do cliente");
                // Obtendo primeira mensagem (Zmsg)
                Optional<ZFrame> op = mensagem.stream().findFirst();
                if (op.isPresent()) {
                    ZFrame primeiroFrame = op.get();
                    // Verificando qual operação foi invocada pelo cliente
                    switch (primeiroFrame.getString(ZMQ.CHARSET)) {
                        case "adicionar":
                            // Cada frame subsequente irá conter uma Pessoa representada em JSON
                            msgResposta.add("adicionado");
                            mensagem.stream().skip(1).forEach(frame -> {
                                String json = frame.getString(ZMQ.CHARSET);
                                System.err.println("Processando JSON recebido: " + json);
                                Pessoa p = gson.fromJson(json, Pessoa.class);
                                String resposta = "Existe uma pessoa com o id (" + p.getId() + ") na base.";
                                if (agenda.get(p.getId()) == null) {
                                    agenda.put(p.getId(), p);
                                    resposta = "id (" + p.getId() + "), pessoa adicionada com sucesso";
                                }
                                System.err.println(resposta);
                                // adicionando um frame por Pessoa processada
                                msgResposta.add(resposta);
                            });
                            break;
                        case "buscar":
                            Optional<ZFrame> frameIdPessoa = mensagem.stream().skip(1).findFirst();
                            if (frameIdPessoa.isPresent()) {
                                int idPessoa = Integer.parseInt(frameIdPessoa.get().getString(ZMQ.CHARSET));
                                Pessoa resultado = agenda.get(idPessoa);
                                if (resultado != null) {
                                    msgResposta.add("encontrada");
                                    msgResposta.add(gson.toJson(resultado));
                                    System.err.println("id: " + idPessoa + ", pessoa encontrada.");
                                } else {
                                    msgResposta.add("não encontrada");
                                }
                            }
                            break;
                        default:
                            System.err.println("cliente invocou uma operação desconhecida");
                            msgResposta.add("Operação desconhecida");
                    }
                    // Enviando a resposta
                    msgResposta.send(socket);
                    System.err.println("====================================");
                }
            }
        }
    }
}
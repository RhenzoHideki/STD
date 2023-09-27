/**
 * Recebe dois inteiros, em duas mensagens separadas, e envia um inteiro como
 * resposta.
 */
#include <arpa/inet.h>
#include <netinet/in.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <unistd.h>

int main(void) {
  int socket_desc, conexao, c;
  struct sockaddr_in servidor, cliente;
  char *cliente_ip;
  int cliente_port;

  int32_t operando1;
  int32_t operando2;
  int32_t resultado;

  socket_desc = socket(AF_INET, SOCK_STREAM, 0);
  if (socket_desc == -1) {
    printf("Nao foi possível criar o socket\n");
    return -1;
  }

  int reuso = 1;
  if (setsockopt(socket_desc, SOL_SOCKET, SO_REUSEADDR, (const char *)&reuso,
                 sizeof(reuso)) < 0) {
    perror("Não foi possível reusar endereço");
    return -1;
  }

#ifdef SO_REUSEPORT
  if (setsockopt(socket_desc, SOL_SOCKET, SO_REUSEPORT, (const char *)&reuso,
                 sizeof(reuso)) < 0) {
    perror("Não foi possível reusar porta");
    return -1;
  }
#endif

  servidor.sin_family = AF_INET;
  servidor.sin_port = htons(1234);
  // Obtem IP do S.O.
  servidor.sin_addr.s_addr = INADDR_ANY;

  // Associando o socket a porta e endereco
  if (bind(socket_desc, (struct sockaddr *)&servidor, sizeof(servidor)) < 0) {
    perror("Erro ao fazer bind\n");
    return -1;
  }
  puts("Bind efetuado com sucesso\n");

  // Ouvindo por conexões
  listen(socket_desc, 3);

  /*********************************************************/
  // Aceitando e tratando conexões
  puts("Aguardando por conexões... \n(pressione CTRL+C para encerrar o "
       "processo)\n\n");
  c = sizeof(struct sockaddr_in);
  conexao = accept(socket_desc, (struct sockaddr *)&cliente, (socklen_t *)&c);
  if (conexao < 0) {
    perror("Erro ao receber conexão\n");
    return -1;
  }
  /*********************************************************/

  /********* comunicação entre cliente e servidor ****************/

  cliente_ip = inet_ntoa(cliente.sin_addr);
  cliente_port = ntohs(cliente.sin_port);
  printf("cliente conectou\nIP:PORTA -> %s:%d\n", cliente_ip, cliente_port);

  // lendo dados enviados pelo cliente
  if ((read(conexao, &operando1, sizeof(operando1))) < 0) {
    printf("Falha ao receber operando 1\n");
    return -1;
  }

  operando1 = ntohl(operando1);

  printf("Operando 1 recebido: %d\n", operando1);
  // recebendo o operando 2
  if ((read(conexao, &operando2, sizeof(operando2))) < 0) {
    printf("Falha ao receber operando 2\n");
    return -1;
  }
  operando2 = ntohl(operando2);
  printf("Operando 2 recebido: %d\n", operando2);

  // realizando a operação
  resultado = operando1 + operando2;

  printf("Resultado: %d\n", resultado);

  resultado = htonl(resultado);

  // enviando o resultado
  //   https://man7.org/linux/man-pages/man2/send.2.html
  if (write(conexao, &resultado, sizeof(resultado)) < 0) {
    printf("Erro ao enviar mensagem\n");
    return -1;
  }
  /*********************************************************/

  // http://www.gnu.org/software/libc/manual/html_node/Closing-a-Socket.html
  shutdown(socket_desc, 2);

  printf("Servidor finalizado...\n");
  return 0;
}
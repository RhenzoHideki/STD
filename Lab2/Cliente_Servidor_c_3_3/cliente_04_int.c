/**
Envia dois inteiros, em duas mensagens separadas, e recebe um inteiro como
resposta
*/
#include <arpa/inet.h>
#include <netdb.h>
#include <netinet/in.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <unistd.h>

#define MAX_MSG 1024
/*
    Cliente envia mensagem ao servidor e imprime resposta
    recebida do Servidor
*/

/* Resolve nome para IP */
const char *nome_para_ip(char *hostname)
{
  struct hostent *he;
  struct in_addr **addr_list;
  int i;

  if ((he = gethostbyname(hostname)) == NULL)
  {
    herror("Nao foi possivel resolver: ");
    return "";
  }

  // convertendo h_addr_list para in_addr
  addr_list = (struct in_addr **)he->h_addr_list;

  for (i = 0; addr_list[i] != NULL; i++)
  {
    // Retorna o primeiro IP e ignora o resto;
    printf("%s resolvido para : %s\n", hostname, inet_ntoa(*addr_list[i]));
    return inet_ntoa(*addr_list[i]);
  }

  return "";
}

int main(int argc, char *argv[])
{
  int socket_desc;
  struct sockaddr_in servidor;

  int32_t operando1 = htonl(10);
  int32_t operando2 = htonl(20);
  int32_t resultado = 0;

  socket_desc = socket(AF_INET, SOCK_STREAM, 0);
  servidor.sin_family = AF_INET;
  servidor.sin_port = htons(1234);
  // servidor.sin_addr.s_addr = inet_addr(nome_para_ip("localhost"));
  //  Se desejar informar o IP, comente a linha acima e descomente a linha abaixo
  servidor.sin_addr.s_addr = inet_addr(argv[1]);

  // Conectando no servidor
  if (connect(socket_desc, (struct sockaddr *)&servidor, sizeof(servidor)) <
      0)
  {
    printf("Nao foi possível conectar\n");
    return -1;
  }
  printf("Conectado no servidor\n");
  /*****************************************/
  // Enviando o operando 1
  //   https://man7.org/linux/man-pages/man2/send.2.html
  if (send(socket_desc, &operando1, sizeof(operando1), 0) < 0)
  {
    printf("Erro ao enviar mensagem\n");
    return -1;
  }
  printf("Operando 1 enviado\n");
  // Enviando o operando 2
  if (write(socket_desc, &operando2, sizeof(operando2)) < 0)
  {
    printf("Erro ao enviar mensagem\n");
    return -1;
  }
  printf("Operando 2 enviado\n");

  // Recebendo resposta do servidor
  if ((read(socket_desc, &resultado, sizeof(resultado))) < 0)
  {
    printf("Falha ao receber resposta\n");
    return -1;
  }
  printf("Resultado recebido: %d\n", ntohl(resultado));
  /*****************************************/

  // fechando o socket
  close(socket_desc);
  printf("Cliente finalizado com sucesso!\n");
  return 0;
}
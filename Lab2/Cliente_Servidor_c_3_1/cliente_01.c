#include <sys/socket.h>
#include <sys/types.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include <stdlib.h>
#include <netdb.h>

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
    // variaveis
    int socket_desc;
    struct sockaddr_in servidor;
    char *mensagem;
    char resposta_servidor[MAX_MSG];
    int tamanho;

    /*****************************************/
    /* Criando um socket */
    // AF_INET = ARPA INTERNET PROTOCOLS
    // SOCK_STREAM = orientado a conexao
    // 0 = protocolo padrao para o tipo escolhido -- TCP
    socket_desc = socket(AF_INET, SOCK_STREAM, 0);

    /* Informacoes para conectar no servidor */

    // familia ARPANET
    servidor.sin_family = AF_INET;

    // Porta - hton = host to network short (2bytes)
    servidor.sin_port = htons(1234);

    // Informando o hostaname
    servidor.sin_addr.s_addr = inet_addr(nome_para_ip("localhost"));
    // Se desejar informar o IP, comente a linha acima e descomente a linha abaixo
    servidor.sin_addr.s_addr = inet_addr("191.36.13.73");

    // Conectando no servidor
    if (connect(socket_desc, (struct sockaddr *)&servidor, sizeof(servidor)) < 0)
    {
        printf("Nao foi possivel conectar\n");
        return -1;
    }
    printf("Conectado no servidor\n");
    /*****************************************/

    /******* TROCA DE MENSAGENS **************/
    // Protocolo definido para essa aplicacao
    // 1 - Cliente conecta
    // 2 - Cliente envia mensagem
    // 3 - Servidor envia resposta

    // Enviando uma mensagem
    mensagem = "Oi servidor\n";
    if (send(socket_desc, mensagem, strlen(mensagem), 0) < 0)
    {
        printf("Erro ao enviar mensagem\n");
        return -1;
    }
    puts("Dados enviados\n");

    // Recebendo resposta do servidor
    if ((tamanho = recv(socket_desc, resposta_servidor, MAX_MSG, 0)) < 0)
    {
        printf("Falha ao receber resposta\n");
        return -1;
    }
    printf("Resposta recebida: ");

    // adicionando fim de linha na string
    resposta_servidor[tamanho] = '\0';
    puts(resposta_servidor);
    /*****************************************/

    // fechando o socket
    close(socket_desc);
    printf("Cliente finalizado com sucesso!\n");
    return 0;
}

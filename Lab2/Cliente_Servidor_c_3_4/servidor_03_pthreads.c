#include <sys/socket.h>
#include <sys/types.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include <stdlib.h>
#include <pthread.h>

#define MAX_MSG 1024

/*
    Servidor ECHO multithread

    - Espera cliente conectar
    - Cria nova thread para o cliente
    - Envia mensagem de boas vindas para o cliente
    - Espera mensagem do cliente
    - Envia a mensagem de echo para o cliente

    Para compilar: gcc -pthread servidor_03_pthreads.c -o servidor
*/

void *tratador_conexao(void *);

void *tratador_conexao(void *conexao)
{
    int sock = *(int *)conexao;
    int tamanho;
    char recebida[MAX_MSG];
    char *mensagem;

    // Enviando mensagem para o cliente
    mensagem = "Servido> Bem vindo.\n";
    write(sock, mensagem, strlen(mensagem));

    while ((tamanho = recv(sock, recebida, MAX_MSG, 0)) > 0)
    {
        // devolvendo a mensagem para o cliente
        recebida[tamanho] = '\0';
        write(sock, recebida, strlen(recebida));
    }

    if (tamanho == 0)
    {
        puts("Cliente desconectou.\n");
        fflush(stdout);
    }
    else if (tamanho == -1)
    {
        perror("erro no recebimento: ");
    }

    // Liberando o ponteiro
    free(conexao);

    return 0;
}

int main(void)
{
    int socket_desc, conexao, c, *nova_conexao;
    struct sockaddr_in server, client;
    char *mensagem;
    char resposta[MAX_MSG];
    int nbytes, count;

    // para pegar o IP e porta do cliente
    char *client_ip;
    int client_port;

    /*********************************************************/
    // Criando um socket
    socket_desc = socket(AF_INET, SOCK_STREAM, 0);
    if (socket_desc == -1)
    {
        printf("Nao foi possivel criar o socket\n");
        return -1;
    }

    int reuso = 1;
    if (setsockopt(socket_desc, SOL_SOCKET, SO_REUSEADDR, (const char *)&reuso, sizeof(reuso)) < 0)
    {
        perror("Não foi possível reusar endereço");
        return -1;
    }
#ifdef SO_REUSEPORT
    if (setsockopt(socket_desc, SOL_SOCKET, SO_REUSEPORT, (const char *)&reuso, sizeof(reuso)) < 0)
    {
        perror("Não foi possível reusar porta");
        return -1;
    }
#endif

    // Preparando a struct do socket
    server.sin_family = AF_INET;
    // Ficará ouvindo na porta 1234
    server.sin_port = htons(1234);
    // Obtem IP do S.O.
    server.sin_addr.s_addr = INADDR_ANY; 
    

    // Associando o socket a porta e endereco
    if (bind(socket_desc, (struct sockaddr *)&server, sizeof(server)) < 0)
    {
        puts("Erro ao fazer bind\n");
    }
    puts("Bind efetuado com sucesso\n");

    // Ouvindo por conexoes
    listen(socket_desc, 3);

    /*********************************************************/
    // Aceitando e tratando conexoes
    puts("Aguardando por conexoes... \n(pressione CTRL+C para encerrar o processo)\n\n");
    c = sizeof(struct sockaddr_in);
    // Fica esperando por conexoes
    while ((conexao = accept(socket_desc, (struct sockaddr *)&client, (socklen_t *)&c)))
    {
        if (conexao < 0)
        {
            perror("Erro ao receber conexao\n");
            return -1;
        }
        // pegando IP e porta do cliente
        client_ip = inet_ntoa(client.sin_addr);
        client_port = ntohs(client.sin_port);
        printf("Cliente conectou: %s : [ %d ]\n", client_ip, client_port);

        /**** Criando thread para tratar da comunicacao ******/
        pthread_t processo;
        nova_conexao = malloc(1);
        *nova_conexao = conexao;

        if (pthread_create(&processo, NULL, tratador_conexao, (void *)nova_conexao) < 0)
        {
            perror("nao foi possivel criar thread: ");
            return -1;
        }

    } // fim do while

    /*********************************************************/

    close(socket_desc);
    shutdown(socket_desc, 2);

    printf("Servidor finalizado...\n");
    return 0;
}

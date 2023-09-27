#include <sys/socket.h>
#include <sys/types.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include <stdlib.h>

#define MAX_MSG 1024

/*
    Servidor recebe mensagem do cliente, imprime na tela e envia uma resposta.
    Se o cliente desconectar, processo do servidor continua ativo esperando
    por novas conexoes.
 */

int main(void)
{

    int socket_desc, conexao, c;
    struct sockaddr_in server, client;
    char *mensagem;
    char resposta[MAX_MSG];
    int tamanho, count;
    int total_clientes_atendidos = 0;

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

        /*********************************************************/

        /********* comunicao entre cliente e servidor ****************/

        // pegando IP e porta do cliente
        client_ip = inet_ntoa(client.sin_addr);
        client_port = ntohs(client.sin_port);
        printf("Cliente %d conectou -> %s:%d\n", ++total_clientes_atendidos, client_ip, client_port);

        // lendo dados enviados pelo cliente
        if ((tamanho = read(conexao, resposta, MAX_MSG)) < 0)
        {
            perror("Erro ao receber dados do cliente: ");
            return -1;
        }

        // Coloca terminador de string
        resposta[tamanho] = '\0';
        printf("O cliente %d falou: %s\n\n", total_clientes_atendidos, resposta);

        // Enviando resposta para o cliente
        mensagem = "Ola cliente, tudo bem?\n";
        write(conexao, mensagem, strlen(mensagem));
    } // fim do while

    /*********************************************************/

    // http://www.gnu.org/software/libc/manual/html_node/Closing-a-Socket.html
    shutdown(socket_desc, 2);

    printf("Servidor finalizado...\n");
    return 0;
}

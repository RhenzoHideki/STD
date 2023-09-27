#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <sys/types.h>

#define TAMBUFFER 512
#define PORTA 1234

int main(void)
{
    struct sockaddr_in addr_local;
    struct sockaddr_in addr_remoto;
    int socket_desc;
    socklen_t slen = sizeof(addr_remoto);
    char buf[TAMBUFFER];
    int tam_recebido;

    // Criando um socket UDP
    if ((socket_desc = socket(AF_INET, SOCK_DGRAM, 0)) < 0)
    {
        perror("nao foi possivel criar socket");
        return -1;
    }

    // ligando o socket ao IP e porta
    memset((char *)&addr_local, 0, sizeof(addr_local));
    addr_local.sin_family = AF_INET;
    addr_local.sin_addr.s_addr = htonl(INADDR_ANY);
    addr_local.sin_port = htons(PORTA);
    if (bind(socket_desc, (struct sockaddr *)&addr_local, sizeof(addr_local)) < 0)
    {
        perror("erro ao fazer bind");
        return -1;
    }

    while (1)
    {
        printf("Aguardando...\n(pressione CTRL+C para encerrar o processo)\n\n");
        // processando pacote recebido
        if ((tam_recebido = recvfrom(socket_desc, buf, TAMBUFFER, 0, (struct sockaddr *)&addr_remoto, &slen)) > 0)
        {
            buf[tam_recebido] = '\0';
            printf("Pacote recebido de: %s:[%d]\nDados: %s\n\n",
                   inet_ntoa(addr_remoto.sin_addr), ntohs(addr_remoto.sin_port), buf);

            // respondendo ao addr_remoto
            if ((sendto(socket_desc, buf, strlen(buf), 0, (struct sockaddr *)&addr_remoto, slen)) < 0)
            {
                perror("erro ao enviar resposta");
            }
        }
    }

    close(socket_desc);
    return 0;
}

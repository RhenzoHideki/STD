#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <sys/types.h>

#define TAMBUFFER 512
#define PORTA 1234

int main(int argc, char *argv[])
{
    struct sockaddr_in addr_local;
    struct sockaddr_in addr_remoto;
    int socket_desc;
    socklen_t slen = sizeof(addr_remoto);
    char buf[TAMBUFFER];
    int tam_recebido;
    char *servidor = argv[1];

    // Criando um socket UDP
    if ((socket_desc = socket(AF_INET, SOCK_DGRAM, 0)) == -1)
        printf("socket created\n");
    // Ligando o socket a todos IPs locais e escolhendo qualquer porta
    memset((char *)&addr_local, 0, sizeof(addr_local));
    addr_local.sin_family = AF_INET;
    addr_local.sin_addr.s_addr = htonl(INADDR_ANY);
    addr_local.sin_port = htons(0);
    if (bind(socket_desc, (struct sockaddr *)&addr_local, sizeof(addr_local)) < 0)
    {
        perror("erro ao fazer bind");
        return -1;
    }
    // Definindo addr_remoto como o endereco onde quero me conectar
    // Convertendo a string 127.0.0.1 para formato binario com inet_aton
    memset((char *)&addr_remoto, 0, sizeof(addr_remoto));
    addr_remoto.sin_family = AF_INET;
    addr_remoto.sin_port = htons(PORTA);
    if (inet_aton(servidor, &addr_remoto.sin_addr) == 0)
    {
        fprintf(stderr, "inet_aton() falhou\n");
        return -1;
    }
    // Enviando mensagem para o servidor
    printf("Enviando mensagem para o servidor: %s [%d]\n", servidor, PORTA);
    sprintf(buf, "Ola, tudo bem?");
    if (sendto(socket_desc, buf, strlen(buf), 0, (struct sockaddr *)&addr_remoto, slen) == -1)
    {
        perror("Erro ao enviar pacote");
        return -1;
    }
    // Recebendo resposta do servidor
    if ((tam_recebido = recvfrom(socket_desc, buf, TAMBUFFER, 0, (struct sockaddr *)&addr_remoto, &slen)) >= 0)
    {
        buf[tam_recebido] = '\0';
        printf("Mensagem recebida: %s \n", buf);
    }
    close(socket_desc);
    return 0;
}

#include<stdio.h>
#include<stdlib.h>
#include<unistd.h>
#include<errno.h>
#include<sys/types.h>
#include<sys/socket.h>
#include<netinet/in.h>
#include <arpa/inet.h> 
#include<string.h>

#define MYPORT 8080


#define ERR_EXIT(m) \
    do { \
    perror(m); \
    exit(EXIT_FAILURE); \
    } while (0)

void echo_ser(int sock)
{
	char recvbuf[1024] = { 0 };
	char dest[1024] = { 0 };
	char error[1024] = { 0 };
	struct sockaddr_in peeraddr;
	socklen_t peerlen;
	int n;

	while (1)
	{

		peerlen = sizeof(peeraddr);
		memset(recvbuf, 0, sizeof(recvbuf));
		n = recvfrom(sock, recvbuf, sizeof(recvbuf), 0,
			(struct sockaddr *)&peeraddr, &peerlen);
		if (n <= 0)
		{

			if (errno == EINTR)
				continue;

			ERR_EXIT("recvfrom error");
		}
		else if (n > 0)
		{

			printf("[From IP address %s:%d] %s\n", inet_ntoa(peeraddr.sin_addr), ntohs(peeraddr.sin_port), recvbuf);
			//printf("port: %d\n", ntohs(peeraddr.sin_port));
			//printf("received data->%s\n", recvbuf);
			//substirng
			memset(dest, '\0', sizeof(dest));
			strncpy(dest, recvbuf, 12);
			//printf("%s", dest);
			if (strcmp(dest, "you->server#") == 0) {
				//write to file
				FILE *fp = fopen("log.txt", "a+");
				fprintf(fp, "[From IP address %s:%d] %s\n", inet_ntoa(peeraddr.sin_addr), ntohs(peeraddr.sin_port), recvbuf);
				//fputs("This is testing for fputs...\n", fp);
				fclose(fp);
				sendto(sock, recvbuf, n, 0,
					(struct sockaddr *)&peeraddr, peerlen);
				printf("send back data->%s\n", recvbuf);
			}
			else {
				//write to file
				FILE *fp = fopen("log.txt", "a+");
				fprintf(fp, "[From IP address %s:%d] Error Format\n", inet_ntoa(peeraddr.sin_addr), ntohs(peeraddr.sin_port));
				//fputs("This is testing for fputs...\n", fp);
				fclose(fp);
				strcpy(error, "Error format");
				sendto(sock, error, n, 0,
					(struct sockaddr *)&peeraddr, peerlen);
				printf("send back data->%s\n", error);
			}
		}
	}
	close(sock);
}

int main(void)
{
	int sock;
	if ((sock = socket(PF_INET, SOCK_DGRAM, 0)) < 0)
		ERR_EXIT("socket error");

	struct sockaddr_in servaddr;
	memset(&servaddr, 0, sizeof(servaddr));
	servaddr.sin_family = AF_INET;
	servaddr.sin_port = htons(MYPORT);
	servaddr.sin_addr.s_addr = htonl(INADDR_ANY);

	printf("Monitor %d port\n", MYPORT);
	if (bind(sock, (struct sockaddr *)&servaddr, sizeof(servaddr)) < 0)
		ERR_EXIT("bind error");

	echo_ser(sock);

	return 0;
}

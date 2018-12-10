#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/types.h>
#include <sys/stat.h>


// This line must be included if you want to use multithreading.
// Besides, use "gcc ./tcp_receive.c -lpthread -o tcp_receive" to compile
// your code. "-lpthread" means link against the pthread library.
#include <pthread.h>

// This the "main" function of each worker thread. All worker thread runs
// the same function. This function must take only one argument of type 
// "void *" and return a value of type "void *". 


void *worker_thread(void *arg) {

	//get system working path and create folder
	int ret = 0;
	int connfd = (int)arg;
	struct stat st = { 0 };

	char cwd[4096];

	if (getcwd(cwd, sizeof(cwd)) != NULL) {
		printf("Current working dir: %s\n", cwd);
	}
	else {
		perror("getcwd() error");
	}

	strcat(cwd, "/copy/");
	
	//if no folder created
	if (stat(cwd, &st) == -1) {
		mkdir(cwd, 0700);
	}
	printf("%s", cwd);
	printf("[%d] worker thread started.\n", connfd);
	while (1) {
		char recv_buffer[4096];
		char path[4096];
		strcpy(path, cwd);
		ret = recv(connfd,
			recv_buffer,
			sizeof(recv_buffer),
			0);
		if (ret < 0) {
			// Input / output error.
			printf("[%d] recv() error: %s.\n", connfd, strerror(errno));
			break;
		}
		printf("path: %s\n", path);
		strcat(path, recv_buffer);
		FILE *fp;
		fp = fopen(path, "wb");

		if (fp == NULL)
		{
			printf("%s,%d:fopen file error! \n", __FILE__, __LINE__);
			break;
		}
		printf("start writing file \n");
		printf("accpet file: %s\n", recv_buffer);
		memset(recv_buffer, '\0', 4096);
		strcpy(recv_buffer, "OK");
		ret = send(connfd, recv_buffer, strlen(recv_buffer), 0);
		if (ret <= 0)
		{
			printf("%s,%d:send error! \n", __FILE__, __LINE__);
			break;
		}
		int totalBytes = 0;
		while (1) {
			memset(recv_buffer, '\0', 4096);
			ret = recv(connfd,
				recv_buffer,
				4096,
				0);

			// TODO: Process your message, receive chunks of byte stream, and 
			// write the chunks to a file. Here I just print it on the screen.

			//binary write to file
			ret = fwrite(recv_buffer, 1, ret, fp);
			totalBytes = totalBytes + ret;

			if (ret < 4096)
			{
				printf("finish writing file, received %d bytes \n", totalBytes);
				memset(recv_buffer, '\0', 4096);
				char str[4096];
				sprintf(str, "%d", totalBytes);
				strcpy(recv_buffer, "server has received file of ");
				strcat(recv_buffer, str);
				strcat(recv_buffer, " bytes");
				send(connfd, recv_buffer, strlen(recv_buffer), 0);
				totalBytes = 0;
				break;
			}
			if (ret <= 0) {
				break;
			}


			//printf("[%d]%s", connfd, recv_buffer);
		}
		memset(recv_buffer, '\0', 4096);
		fclose(fp);

	}
	

	printf("[%d] worker thread terminated.\n", connfd);
}


// The main thread, which only accepts new connection. Connection socket
// is handled by the worker thread.
int main(int argc, char *argv[]) {

	int ret;
	socklen_t len;
	int listenfd = 0, connfd = 0;
	struct sockaddr_in serv_addr;
	struct sockaddr_in client_addr;

	listenfd = socket(AF_INET, SOCK_STREAM, 0);
	if (listenfd < 0) {
		printf("socket() error: %s.\n", strerror(errno));
		return -1;
	}

	memset(&serv_addr, 0, sizeof(serv_addr));
	serv_addr.sin_family = AF_INET;
	serv_addr.sin_addr.s_addr = htonl(INADDR_ANY);
	serv_addr.sin_port = htons(6660);

	ret = bind(listenfd, (struct sockaddr*)
		&serv_addr, sizeof(serv_addr));
	if (ret < 0) {
		printf("bind() error: %s.\n", strerror(errno));
		return -1;
	}


	if (listen(listenfd, 10) < 0) {
		printf("listen() error: %s.\n", strerror(errno));
		return -1;
	}

	while (1) {
		printf("waiting for connection...\n");
		connfd = accept(listenfd,
			(struct sockaddr*) &client_addr,
			&len);

		if (connfd < 0) {
			printf("accept() error: %s.\n", strerror(errno));
			return -1;
		}
		printf("conn accept - %s.\n", inet_ntoa(client_addr.sin_addr));

		pthread_t tid;
		pthread_create(&tid, NULL, worker_thread, (void *)connfd);

	}
	return 0;
}

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>


int main(int argc, char *argv[]) {
	int ret;
	int sockfd = 0;
	struct sockaddr_in serv_addr;

	sockfd = socket(AF_INET, SOCK_STREAM, 0);
	if (sockfd < 0) {
		printf("socket() error: %s.\n", strerror(errno));
		return -1;
	}

	// Note that this is the server address that the client will connect to.
	// We do not care the source IP address and port number. 
	memset(&serv_addr, 0, sizeof(serv_addr));
	serv_addr.sin_family = AF_INET;
	serv_addr.sin_addr.s_addr = inet_addr("127.0.0.1");
	serv_addr.sin_port = htons(6660);
	ret = connect(sockfd,
		(struct sockaddr *) &serv_addr,
		sizeof(serv_addr));
	if (ret < 0) {
		printf("connect() error: %s.\n", strerror(errno));
		return -1;
	}

	while (1) {
		char send_buffer[4096];
		char copy_buffer[4096];
		char campare_buffer[4096];
		printf("Please Enter the file to upload: \n");
		scanf("%s", send_buffer);
		// TODO: You need to parse the string you read from the keyboard.
		// If it follows the format "you->server$file_name", extract the
		// file name and open the file, read each chunk and send the
		// chunk. You may need to write an inner loop to read and send
		// each chunk. 

		// You may also need to design a way to tell the length of the
		// file to the receiver, or use a special message to indicate the
		// end of the file.

		// I add two lines to allow the client to "gracefully exit".
		// You not not need to use these lines.
		//send file imformation
		if (strncmp(send_buffer, "exit", strlen("exit")) == 0) {
			break;
		}
		memset(copy_buffer, '\0', sizeof(copy_buffer));
		memset(campare_buffer, '\0', sizeof(campare_buffer));
		strncpy(campare_buffer, send_buffer, 12);
		strncpy(copy_buffer, send_buffer+12, strlen(send_buffer));
		if (strcmp(campare_buffer, "you->server$") != 0) {
			printf("Error format, please using you->server$ at the beginning \n");
			break;
		}

		FILE *fid;
		fid = fopen(copy_buffer, "rb");
		if (fid == NULL) {
			printf("no file found \n");
			break;
		}

		ret = send(sockfd, copy_buffer, strlen(copy_buffer), 0);
		if (ret <= 0)
		{
			printf("%s,%d:send error! \n", __FILE__, __LINE__);
			break;

		}
		memset(copy_buffer, '\0', 4096);
		ret = recv(sockfd, copy_buffer, 4096, 0);
		int totalBytes = 0;
		if (strncmp(copy_buffer, "OK", 2) == 0)
		{
			while (1)
			{
				char *pos = (char*)malloc(sizeof(char) * 4096);
				//memset(copy_buffer, 0, 4096);
				ret = fread(pos, 1, 4096, fid);
				totalBytes = totalBytes + ret;
				if (ret <= 0)// finished reading file 
				{
					if (ret < 0)
					{
						printf("%s,%d:fread file error! \n", __FILE__, __LINE__);
						break;
					}
					printf("finished sending file, send totalBytes %d \n", totalBytes);

					free(pos);
					memset(copy_buffer, '\0', 4096);
					recv(sockfd, copy_buffer, 4096, 0);
					printf("%s\n", copy_buffer);
					break;
				}
				ret = send(sockfd, pos, ret, 0);

				if (ret <= 0)
				{
					printf("%s,%d:send error! \n", __FILE__, __LINE__);
					break;
				}
			}

		}
		fclose(fid);
		memset(send_buffer, '\0', 4096);
	}

	close(sockfd);

	return 0;
}



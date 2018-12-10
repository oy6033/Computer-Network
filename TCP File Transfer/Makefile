PROGS = fileserver fileclient
temp = $(wildcard *~)
all:$(PROGS)
.PHONY:all
fileserver:
	gcc ./tcp_server.c -lpthread -o tcp_server
fileclient:
	gcc -g tcp_client.c -o tcp_client
clean:
	rm $(temp) $(PROGS)

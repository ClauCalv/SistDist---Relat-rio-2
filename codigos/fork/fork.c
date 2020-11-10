#include <sys/types.h>
#include <sys/socket.h>
#include <stdio.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <signal.h>
#include <unistd.h>
#include <string.h>

#define SERVER_PORT 9000

int main() {
    int server_sockfd, client_sockfd;
    int server_len, client_len;
    struct sockaddr_in server_address;
    struct sockaddr_in client_address;

    server_sockfd = socket(AF_INET, SOCK_STREAM, 0);

    server_address.sin_family = AF_INET;
    server_address.sin_addr.s_addr = htonl(INADDR_ANY);
    server_address.sin_port = htons(SERVER_PORT);
    server_len = sizeof(server_address);
    bind(server_sockfd, (struct sockaddr *)&server_address,server_len);

    /* Create a connection queue, ignore child exit details and wait for
    clients. */

    listen(server_sockfd, 5);

    signal(SIGCHLD, SIG_IGN);

    while(1) {
	char ch;

	printf("Waiting for connection at port %d. \n",SERVER_PORT);

	/* Accept connection. */

	client_len = sizeof(client_address);
	client_sockfd = accept(server_sockfd,(struct sockaddr *)&client_address, &client_len);

	/* Fork to create a process for this client and perform a test to see
	whether we're the parent or the child. */

	if(fork() == 0) {

	   /* If we're the child, we can now read/write to the client on client_sockfd. */

	  printf("Connection established from %s, local port: %d, remote port: %d.\n",inet_ntoa(client_address.sin_addr),SERVER_PORT,(int)ntohs(client_address.sin_port));

	  /* Allocate input buffer. */
	  char input[65535];
	  /* Quit command. */ 
	  char quit[65535];
	  strcpy(quit,"quit\n");

	  /** Loop - until a quit command is received do. */
	  do {
	    int length = 0;
	    /* Read a character until a line feed is found. */
	    do {
	       read(client_sockfd, &ch, 1);
	       input[length] = ch;
	       length++;
	    } while (ch != '\n');
	    /* Compare to input string to quit command. */
	    if (strcmp(input,quit)!=0) { 
	      /* End input string. */
	      input[length] = '\0';
	      printf("From client %s:%d: %s",inet_ntoa(client_address.sin_addr),(int)ntohs(client_address.sin_port),input);
	      /* Allocate output. */
	      char output[65535];
	      int i;
	      /* Convert every character to upper case. */
	      for (i=0; i < length; i++) {
	       output[i] = (char) toupper(input[i]);
	      }
	      /* Send the output to the client. */
	      write(client_sockfd, output, length);
	    }
	  } while (strcmp(input,quit)!=0);
	  /* Close communication. */
	  printf("Connection closed from %s:%d \n",inet_ntoa(client_address.sin_addr),(int)ntohs(client_address.sin_port));
	  close(client_sockfd);
	  return 0;
	} /* Otherwise, we must be the parent and our work for this client is
	finished. */
	else {
	    close(client_sockfd);
	}
    }
}

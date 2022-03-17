#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <netdb.h>
#include <arpa/inet.h>

#include "socket.h"
#include "jobprotocol.h"


int main(int argc, char **argv) {
    // This line causes stdout and stderr not to be buffered.
    // Don't change this! Necessary for autotesting.
    setbuf(stdout, NULL);
    setbuf(stderr, NULL);

    if (argc != 2) {
        fprintf(stderr, "Usage: jobclient hostname\n");
        exit(1);
    }

    int soc = connect_to_server(PORT, argv[1]);


    /* TODO: Accept commands from the user, verify correctness 
     * of commands, send to server. Monitor for input from the 
     * server and echo to STDOUT.
     */
    char buf[BUFSIZE + 1];
    
    fd_set set_first,set_copy;
    FD_ZERO(&set_first);
    FD_SET(soc,&set_first);
    FD_SET(STDIN_FILENO,&set_first);
    
    while (1) {
        set_copy = set_first;
        select(soc+1 ,&set_copy,NULL,NULL,NULL);
        
        if (FD_ISSET(STDIN_FILENO,&set_copy)) {
            int num_read = read(STDIN_FILENO, buf, BUFSIZE);
            buf[num_read-1] = '\0';
            //check command validity
            if (strlen(buf) == 0) {
                fprintf(stderr, "Command not found\n");
                continue;
            }
            if (check_command_valid(buf) == -1) {
                fprintf(stderr, "Command not found\n");
                continue;
            }
            if (strcmp(buf,"exit") == 0) {
                close(soc);
                exit(0);
            }
            //append network-newline and send it to server
            buf[num_read-1] = '\r';
            buf[num_read] = '\n';
            int num_written = write(soc, buf, num_read+1);
            if (num_written != num_read+1 || num_written == -1) {
                perror("client: write");
                close(soc);
                exit(1);
            }
        }
        if (FD_ISSET(soc,&set_copy)) {
            char buf[BUFSIZE] = {'\0'};
            int inbuf = 0;           
            int room = sizeof(buf);
            char *after = buf;
            
            int nbytes;
            while ((nbytes = read(soc, after, room)) > 0) {
                inbuf = inbuf + nbytes;
                int where;
            
                while ((where = find_network_newline(buf, inbuf)) > 0) {
                    buf[where-2] = '\n';
                    buf[where- 1]='\0';
                    write(STDOUT_FILENO,&buf,strlen(buf));
                    buf[where-2]='\0';
                    inbuf = inbuf - (strlen(buf)+2);
                    memmove(buf,buf + where,inbuf);
                    if (inbuf == 0){
                        break;
                    }
                }
                if (inbuf == 0){
                    break;
                }
                room = sizeof(buf) - inbuf;
                after = &buf[inbuf];
                
            }
        }
    }
    if(close(soc) == -1){
        perror("client: close socket");
        exit(1);
    }
    return 0;
}

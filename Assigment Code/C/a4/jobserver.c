#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/types.h>
#include <signal.h>
#include <sys/wait.h>

#include "socket.h"
#include "jobprotocol.h"


#define QUEUE_LENGTH 5

#ifndef JOBS_DIR
    #define JOBS_DIR "jobs/"
#endif
void send_message(int fds, int client_fd, int message_type, int job_pid){
    char buf[BUFSIZE]={0};
    int inbuf = 0;
    int room = sizeof(buf);
    char *after = buf;
    int nbytes;
    char server_send[BUFSIZE]={0};
    
    while ((nbytes = read(fds, after, room)) > 0) {
        inbuf = inbuf + nbytes;
        if(message_type == 1){
            buf[inbuf-1] = '\0';
            sprintf(server_send,"*(JOB %d)* %s\r\n",job_pid,buf);
            write(client_fd, server_send, strlen(server_send));
            return;
        }
        int where;
        while ((where = find_newline(buf, inbuf)) > 0) {
            buf[where] = '\0';
            if (buf[where - 1] == '\r') {
                if (message_type == 0) {
                    sprintf(server_send,"[JOB %d] %s\n",job_pid,buf);
                    write(client_fd, server_send, strlen(server_send));
                }
            }else{
                if (message_type == 0) {
                    sprintf(server_send,"[JOB %d] %s\r\n",job_pid,buf);
                    write(client_fd, server_send, strlen(server_send));
                }
            }
    
            inbuf = inbuf - (strlen(buf)+1);
            memmove(buf,buf + where+1,inbuf+1);
        }
        room = sizeof(buf) - inbuf;
        after = &buf[inbuf];
        
    }
    close(fds);
}

void catch(int sig){
    char buf[BUFSIZE];
    for (int i = 0; i < FD_SETSIZE; i++) {
        if (connect_clients[i] > 0) {
            sprintf(buf,"[SERVER] Shutting down\r\n");
            write(connect_clients[i], buf, strlen(buf));
            close(connect_clients[i]);
        }
    }
    free(self);
    close(listenfd);
    exit(0);
}

int inarray(int *array, int value, int size){
    for (int i=0; i < size; i++) {
        if (array[i] == value)
            return 0;
    }
    return 1;
}

int read_from(int client_index, int connect_clients[],struct job *jobsnames) {
    int fd = connect_clients[client_index];

    char buf[BUFSIZE] = {'\0'};
    int inbuf = 0;           
    int room = sizeof(buf);
    char *after = buf;
    char sendbuf[BUFSIZE] = {0};
    int nbytes;
    
    while ((nbytes = read(fd, after, room)) > 0) {
        inbuf = inbuf + nbytes;
        int where;
        while ((where = find_network_newline(buf, inbuf)) > 0) {
            buf[where-2] = '\0';
            printf("[CLIENT %d] %s\n",fd,buf);
            // command is jobs
            int retv = check_command_server(buf);
            if (retv == 1) {
                char char_pid[BUFSIZE];
                char send_pid[BUFSIZE] = "[SERVER]";
                for (int i = 0 ; i < MAX_JOBS; i++) {
                    if (jobsnames[i].job_pid != -1) {
                        if (inarray(jobsnames[i].watch,fd,FD_SETSIZE) == 0) {
                            sprintf(char_pid," %d",jobsnames[i].job_pid);
                            strcat(send_pid, char_pid);
                        }
                    }
                }
                if (strlen(send_pid) == 8) {
                    strcat(send_pid," No currently running jobs");
                }
                //make it newwork newline
                send_pid[strlen(send_pid)] = '\r';
                send_pid[strlen(send_pid)] = '\n';
        
                if (write(fd, send_pid, strlen(send_pid)) != strlen(send_pid)) {
                    connect_clients[client_index] = 0;
                    return fd;
                }
                
            }
            //command is run
            if(retv == 2) {
                int num_jobs = 0;
                for (int index = 0; index < MAX_JOBS; index++) {
                    if (jobsnames[index].job_pid != -1) {
                        num_jobs += 1;
                    }
                }
                if (num_jobs == MAX_JOBS) {
                    sprintf(sendbuf,"[SERVER] MAXJOBS exceeded\r\n");
                    if (write(fd, sendbuf, strlen(sendbuf)) != strlen(sendbuf)) {
                        connect_clients[client_index] = 0;
                        return fd;
                    }
                    return 0;
                }
                int status;
                int retval;
                char *token;
                char *argv[BUFSIZE];
                char exe_file[BUFSIZE];
                token = strtok(buf, " ");
                int i = 0;
                int fds_stdout[2];
                int fds_stderr[2];
                
                while (token != NULL)
                {
                    argv[i] = token;
                    i++;
                    token = strtok (NULL, " ");
                }
                sprintf(exe_file, "%s/%s", JOBS_DIR, argv[0]);
                argv[i]= NULL;
                
                if ((pipe(fds_stdout)) == -1) {
                    perror("pipe");
                    exit(1);
                }
                if ((pipe(fds_stderr)) == -1) {
                    perror("pipe");
                    exit(1);
                }
                if((retval = fork()) < 0){
                    perror("fork");
                    exit(1);
                }
                if(retval == 0){//child
                    if (dup2(fds_stdout[1],STDOUT_FILENO) == -1) {
                        perror("dup");
                        exit(1);
                    }
                    if (dup2(fds_stderr[1],STDERR_FILENO) == -1) {
                        perror("dup");
                        exit(1);
                    }
                    if (close(fds_stdout[0]) == -1) {
                        perror("close");
                        exit(1);
                    }
                    if (close(fds_stdout[1]) == -1) {
                        perror("close");
                        exit(1);
                    }
                    
                    execvp(exe_file,argv);
                    perror("execvp");
                    exit(1);
                }
                if (close(fds_stdout[1]) == -1) {
                    perror("close");
                    exit(1);
                }
                if (close(fds_stderr[1]) == -1) {
                    perror("close");
                    exit(1);
                }
                printf("[SERVER] Job %d created\n",retval);
                for (int index = 0; index < MAX_JOBS; index++) {
                    if (jobsnames[index].job_pid == -1) {
                        jobsnames[index].job_pid = retval;
                        jobsnames[index].watch[0] = fd;
                        break;
                    }
                }
                send_message(fds_stdout[0],fd,0,retval);
                send_message(fds_stderr[0],fd,1,retval);
                
                if (wait(&status) < 0){
                    perror("wait");
                    exit(1);
                }
                
                if (WIFEXITED(status)) {
                    sprintf(sendbuf,"[JOB %d] Exited with status %d.\r\n",retval,WEXITSTATUS(status));
                    for (int index = 0; index < MAX_JOBS; index++) {
                        if (jobsnames[index].job_pid == retval) {
                            for (int j = 0; j < FD_SETSIZE; j++) {
                                if(jobsnames[index].watch[j] != 0){
                                    if (write(jobsnames[index].watch[j], sendbuf, strlen(sendbuf)) != strlen(sendbuf)) {
                                        connect_clients[client_index] = 0;
                                        return fd;
                                    }
                                    
                                }
                            }
                            jobsnames[index].job_pid = -1;
                            memset(jobsnames[index].watch, 0, sizeof(jobsnames[index].watch));
                            break;
                        }
                    }
                    
                }
                return 0;
            }
            //command is kill
            if(retv == 3) {
                pid_t pid = strtol(buf, NULL, 10);
                for (int index = 0; index < MAX_JOBS; index++) {
                    if (jobsnames[index].job_pid == pid) {
                        if(kill(pid,SIGKILL) == -1){
                            perror("kill");
                            exit(1);
                        }
                        sprintf(sendbuf,"[Job %d] Exited due to signal.\r\n",pid);
                        if (write(fd, sendbuf, strlen(sendbuf)) != strlen(sendbuf)) {
                            connect_clients[client_index] = 0;
                            return fd;
                        }
                        jobsnames[index].job_pid = -1;
                        memset(jobsnames[index].watch, 0, sizeof(jobsnames[index].watch));
                        return 0;
                    }
                }
                sprintf(sendbuf,"[SERVER] Job %d not found\r\n",pid);
                if (write(fd, sendbuf, strlen(sendbuf)) != strlen(sendbuf)) {
                    connect_clients[client_index] = 0;
                    return fd;
                }
                return 0;
            }
            //command is watch
            if(retv == 4) {
                pid_t pid = strtol(buf, NULL, 10);
                for (int index = 0; index < MAX_JOBS; index++) {
                    if (jobsnames[index].job_pid == pid) {
                        if (inarray(jobsnames[index].watch,fd,FD_SETSIZE) == -1) {
                            for (int i = 0; i < FD_SETSIZE; i++) {
                                if (jobsnames[index].watch[i] == 0) {
                                    jobsnames[index].watch[i] = fd;
                                    sprintf(sendbuf,"[SERVER] Watching job %d\r\n",pid);
                                    if (write(fd, sendbuf, strlen(sendbuf)) != strlen(sendbuf)) {
                                        connect_clients[client_index] = 0;
                                        return fd;
                                    }
                                    break;
                                }
                            }
                        }else{
                            for (int i = 0; i < FD_SETSIZE; i++) {
                                if (jobsnames[index].watch[i] == fd) {
                                    jobsnames[index].watch[i] = 0;
                                    sprintf(sendbuf,"[SERVER] No longer watching job %d\r\n",pid);
                                    if (write(fd, sendbuf, strlen(sendbuf)) != strlen(sendbuf)) {
                                        connect_clients[client_index] = 0;
                                        return fd;
                                    }
                                    break;
                                }
                            }
                        }
                        
                    }
                }
                return 0;
            }
            if(retv == -1) {
                sprintf(sendbuf,"[SERVER] Invalid command: %s\r\n",buf);
                if (write(fd, sendbuf, strlen(sendbuf)) != strlen(sendbuf)) {
                    connect_clients[client_index] = 0;
                    return fd;
                }
                
            }
            
            inbuf = inbuf - (strlen(buf)+2);
            memmove(buf,buf + where,inbuf);
            if (inbuf == 0){
                return 0;
            }
        }
        room = sizeof(buf) - inbuf;
        after = &buf[inbuf];
        
    }
    connect_clients[client_index] = 0;
    return fd;
}

int main(void) {
    // This line causes stdout and stderr not to be buffered.
    // Don't change this! Necessary for autotesting.
    setbuf(stdout, NULL);
    setbuf(stderr, NULL);

    self = init_server_addr(PORT);
    listenfd = setup_server_socket(self, QUEUE_LENGTH);
    

    /* TODO: Initialize job and client tracking structures, start accepting
     * connections. Listen for messages from both clients and jobs. Execute
     * client commands if properly formatted. Forward messages from jobs
     * to appropriate clients. Tear down cleanly.
     */
    struct job jobsnames[MAX_JOBS];
    for (int index = 0; index < MAX_JOBS; index++) {
        jobsnames[index].job_pid = -1;
    }
    
    int max_fd = listenfd;
    fd_set set_first, set_copy;
    FD_ZERO(&set_first);
    FD_SET(listenfd, &set_first);
    
    while (1) {
        set_copy = set_first;
        int retv = select(max_fd + 1, &set_copy, NULL, NULL, NULL);
        if (retv == -1) {
            perror("server: select");
            exit(1);
        }
        
        // Create a new connection added it to connect list
        if (FD_ISSET(listenfd, &set_copy)) {
            char buf[BUFSIZE];
            int client_fd = accept_connection(listenfd);
            if (client_fd > max_fd) {
                max_fd = client_fd;
            }
            for (int i = 0; i < FD_SETSIZE; i++)
            {
                if( connect_clients[i] == 0 )
                {
                    connect_clients[i] = client_fd;
                    break;
                }
            }
            FD_SET(client_fd, &set_first);
            sprintf(buf,"[SERVER]: Accepted connection!\r\n");
            write(client_fd, buf, strlen(buf));
        }
        
        for (int i = 0; i < FD_SETSIZE; i++){
            if (connect_clients[i] > 0 && FD_ISSET(connect_clients[i], &set_copy)){
                int client_closed = read_from(i, connect_clients,jobsnames);
                if (client_closed > 0) {
                    FD_CLR(client_closed, &set_first);
                    close(client_closed);
                    printf("[CLIENT %d] Connection closed\n", client_closed);
                }
            }
        }
        signal(SIGINT,catch);
    }


    /* Here is a snippet of code to create the name of an executable to execute:
     * char exe_file[BUFSIZE];
     * sprintf(exe_file, "%s/%s", JOBS_DIR, <job_name>);
     */

    exit(0);
}


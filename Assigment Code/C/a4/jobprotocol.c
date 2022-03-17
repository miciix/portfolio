#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "jobprotocol.h"
// TODO: Use this file for helper functions (especially those you want available
// to both executables.
int find_network_newline(const char *buf, int inbuf) {
    for (int i = 0; i < inbuf; i++) {
        if (*(buf + i) == '\r' && *(buf + 1 + i) == '\n') {
            return i + 2;
        }
    }
    return -1;
}
int find_newline(const char *buf, int inbuf) {
    for (int i = 0; i < inbuf; i++) {
        if (*(buf + i) == '\n') {
            return i ;
        }
    }
    return -1;
}
int check_command_valid(char *buf){
    char commands[BUFSIZE];
    strcpy(commands,buf);
    const char *command[5];
    command[0] = "exit";
    command[1] = "run";
    command[2] = "kill";
    command[3] = "watch";
    command[4] = "jobs";

    char command_check[BUFSIZE];
    char *token;
    token = strtok(commands, " ");
    strcpy(command_check,token);
    
    for (int i = 0; i < 5; i++) {
        if (strcmp(command_check,command[i]) == 0) {
            
            return 0;
        }
    }
    return -1;
}

int check_command_server(char *buf){
    int count = 0;
    char commands[BUFSIZE];
    strcpy(commands,buf);
    char command_check[BUFSIZE];
    char *token;
    //command
    token = strtok(commands, " ");
    strcpy(command_check,token);
    
    //arguments
    char *arguments;
    token = strtok (NULL, " ");
    
    if (strcmp(command_check,"jobs") == 0) {
        if (token == NULL) {
            return 1;
        }else{
            return -1;
        }
    }
    if (strcmp(command_check,"run") == 0) {
        if(token == NULL)
        {
            return -1;
        }else{
            strcpy(commands,buf);
            arguments = commands;
            token = strtok(commands, " ");
            arguments = arguments+strlen(token)+1;
            strcpy(buf,arguments);
            return 2;
        }

    }
    if (strcmp(command_check,"kill") == 0) {
        while (token != NULL)
        {
            count += 1;
            token = strtok (NULL, " ");
        }
        if (count == 1) {
            strcpy(commands,buf);
            token = strtok(commands, " ");
            token = strtok (NULL, " ");
            strcpy(buf,token);
            return 3;
        }else{
            return -1;
        }
    }
    if (strcmp(command_check,"watch") == 0) {
        while (token != NULL)
        {
            count += 1;
            token = strtok (NULL, " ");
        }
        if (count == 1) {
            strcpy(commands,buf);
            token = strtok(commands, " ");
            token = strtok (NULL, " ");
            strcpy(buf,token);
            return 4;
        }else{
            return -1;
        }
    }
    return -1;
}

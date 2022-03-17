#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include "eratosthenes.h"
pid_t make_stage(int m, int read_fd, int **fds){
    int retval;
    //create pipe
    if ((pipe(*fds)) == -1) {
        perror("pipe");
        exit(-1);
    }
    //create fork
    if((retval = fork()) < 0){
        perror("fork");
        exit(-1);
    }
    if (retval == 0) {//child
        //close write of pipe since child don't need to write
        if (close((*fds)[1]) == -1) {
            perror("close");
            exit(-1);
        }
        return 0;
    }
    //only parent get here
    //close read of pipe since parent do not need to read
    if (close((*fds)[0]) == -1) {
        perror("close");
        exit(-1);
    }
    if (filter(m,read_fd,(*fds)[1]) == -1){
        fprintf(stderr,"fail to filter");
        exit(-1);
    }
    //finished filtering previous of pipe close read
    if (close(read_fd) == -1) {
        perror("close");
        exit(-1);
    }
    //finished writing close write of pipe
    if (close((*fds)[1]) == -1) {
        perror("close");
        exit(-1);
    }
    return getpid();
}

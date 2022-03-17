#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>

int filter(int m, int readfd, int writefd){
    int integer;
    int err;
    while ((err = read(readfd, &integer,sizeof(int))) > 0) {
        if (integer % m != 0) {
            write(writefd, &integer, sizeof(int));
        }
    }
    if (err < 0) {
        perror("read");
        return -1;
    }
    return  0;
}

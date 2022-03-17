#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include <unistd.h>
#include <signal.h>
#include <sys/wait.h>
#include <math.h>

#include "eratosthenes.h"


int main(int argc, char *argv[]) {
    // Turning off sigpipe
    if (signal(SIGPIPE, SIG_IGN) == SIG_ERR) {
        perror("signal");
        exit(1);
    }

    // Your solution below ...
    char *ptr;
    //Wrong number of arguments
    if (argc != 2){
        fprintf(stderr, "Usage:\n\tpfact n\n");
        exit(1);
    }
    int n = strtol(argv[1],&ptr,10);
    if (strlen(ptr) > 0) {
        fprintf(stderr, "Usage:\n\tpfact n\n");
        exit(1);
    }
    //Argument that is not a positive integer
    if(n <= 0){
        fprintf(stderr, "Usage:\n\tpfact n\n");
        exit(1);
    }
    
    int r;
    int fds[2];
    int *p_fds = fds;
    if ((pipe(fds)) == -1) {
        perror("pipe");
        exit(2);
    }
    if((r = fork()) < 0){
        perror("fork");
        exit(2);
    }
    if (r == 0) {
        int prime;
        int err;
        int make_stage_pid;
        int count = 0;
        int status;
        int factor_1 = 0;
        int factor_2 = 0;
        //close write of pipe since child don't need to write
        if (close(fds[1]) == -1) {
            perror("close");
            exit(-1);
        }
        // read 2
        read(fds[0], &prime,sizeof(int));// closed in make_stage
        if ((n % prime) == 0 && n != 2) {
            factor_1 = prime;
        }
        
        if(factor_1 * factor_1 == n){
            printf("%d %d %d\n", n, factor_1, factor_1);
            exit(count);
        }

        if((n % prime) == 0){
            if ((n/prime)%prime == 0) {
                printf("%d is not the product of two primes\n", n);
                exit(count);
            }
        }
        
        while(prime < sqrt(n)) {
            make_stage_pid = make_stage(prime,fds[0],&p_fds);
            count += 1;
            if (make_stage_pid == 0) {//child
                err = read(fds[0], &prime,sizeof(int));
                if (err == -1) {
                    perror("read");
                    exit(-1);
                }
                //check if prime is one of factor of n
                if (n % prime == 0){
                    if (factor_1 == 0) {
                        factor_1 = prime;
                    }else{
                        factor_2 = prime;
                    }
                }
                //exmple: 25 5 5
                if(factor_1 * factor_1 == n){
                    printf("%d %d %d\n", n, factor_1, factor_1);
                    exit(count);
                }
                //exmple: 125
                if((n % prime) == 0){
                    if ((n/prime)%prime == 0) {
                        printf("%d is not the product of two primes\n", n);
                        exit(count);
                    }
                }
                //n have factor 1 and factor 2
                if (factor_1 != 0 && factor_2 != 0) {
                    if (factor_2 * factor_1 == n) {
                        printf("%d %d %d\n", n, factor_1, factor_2);
                        exit(count);
                    }else{
                        printf("%d is not the product of two primes\n", n);
                        exit(count);
                    }
                }
            }else if(make_stage_pid >0){//parent
                if (wait(&status) < 0){
                    perror("wait");
                    exit(-1);
                }
                exit(WEXITSTATUS(status));
            }
        }
        if (factor_1 == 0 && factor_2 == 0) {
            printf("%d is prime\n", n);
            exit(count);
        }else{
            if (n % prime == 0 && factor_2 * factor_1 == n) {
                factor_2 = prime;
                printf("%d %d %d\n", n, factor_1, factor_2);
                exit(count);
            }
            while ((err = read(fds[0], &prime,sizeof(int))) > 0) {
                if (n % prime == 0) {
                    factor_2 = prime;
                    if (factor_1 * factor_2 == n) {
                        printf("%d %d %d\n", n, factor_1, factor_2);
                        exit(count);
                    }else{
                        printf("%d is not the product of two primes\n", n);
                        exit(count);
                    }
                }
            }
            if (err < 0) {
                perror("read");
                exit(-1);
            }
            //TODO check rest of prime in pipe
            printf("%d is not the product of two primes\n", n);
            exit(count);
        }
    }else if (r > 0){
        int child_status;
        //close read of pipe since parent do not need to read
        if (close(fds[0]) == -1) {
            perror("close");
            exit(2);
        }
        for (int i = 2; i <= n; i ++) {
            write(fds[1], &i, sizeof(int));
        }
        //finished writing close write of pipe
        if (close(fds[1]) == -1) {
            perror("close");
            exit(2);
        }
        if (wait(&child_status) < 0){
            perror("wait");
            exit(2);
        }
        if (WIFEXITED(child_status)) {
            if (WEXITSTATUS(child_status) == -1) {
                exit(2);
            }else{
                printf("Number of filters = %d\n", WEXITSTATUS(child_status));
                exit(0);
            }
        }else{
            exit(2);
        }
    }
    return 0;
}

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include "ptree.h"


int main(int argc, char **argv) {
    // TODO: Update error checking and add support for the optional -d flag
    // printf("Usage:\n\tptree [-d N] PID\n");
    
    // NOTE: This only works if no -d option is provided and does not
    // error check the provided argument or generate_ptree. Fix this!
    struct TreeNode *root = NULL;
    long depth = 0;
    long pid;
		char *ptr;
		int len;
    
    if (argc > 4 || argc < 2 || argc == 3) {
        fprintf(stderr,"ERROR: Invalid number of arguments.\n");
        return 1;
    }
    if (argc == 2) {
        errno = 0;
        pid = strtol(argv[1], NULL, 10);
        if (errno != 0) {
            fprintf(stderr,"ERROR: Unable to convert the pid\n");
            return 1;
        }
    }
    if (argc == 4) {
        if(strcmp(argv[1], "-d") == 0) {
            errno = 0;
            depth = strtol(argv[2], &ptr, 10);
						len = strlen(ptr);
						if(len > 0){
								fprintf(stderr,"ERROR: Unable to convert the flag argument\n");
								return 1;
						}
						if (errno != 0) {
                fprintf(stderr,"ERROR: Unable to convert the flag argument\n");
                return 1;
            }
            errno = 0;
            pid = strtol(argv[3], NULL, 10);
            if (errno != 0) {
                fprintf(stderr,"ERROR: Unable to convert the pid\n");
                return 1;
            }
        }else{
            errno = 0;
            pid = strtol(argv[1], NULL, 10);
            if (errno != 0) {
                fprintf(stderr,"ERROR: Unable to convert the pid\n");
                return 1;
            }
            if(strcmp(argv[2], "-d") == 0){
                errno = 0;
                depth = strtol(argv[3], &ptr, 10);
						    len = strlen(ptr);
								if(len > 0){
									fprintf(stderr,"ERROR: Unable to convert the flag argument\n");
									return 1;
								}
                if (errno != 0) {  
               		fprintf(stderr,"ERROR: Unable to convert the flag argument\n");
                  return 1;
                }
            }else{
                fprintf(stderr,"ERROR: Invalid arguments\n");
                return 1;
            }
        }
    }
    
    if(generate_ptree(&root, pid) == 1){
        print_ptree(root, depth);
        return 2;
    }else{
        print_ptree(root, depth);
    }
    
    return 0;
}


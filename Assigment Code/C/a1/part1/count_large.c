#include <stdio.h>
#include <stdlib.h>

// TODO: Implement a helper named check_permissions that matches the prototype below.
int check_permissions(char *file, char *require){
    for(int i = 0 ; i < 9; i++){
        if(*(file + i) != *(require+i) && *(require+i) != '-'){
            return 1;
        }
    }
    return 0;
}

int main(int argc, char** argv) {
    if (!(argc == 2 || argc == 3)) {
        fprintf(stderr, "USAGE:\n\tcount_large size [permissions]\n");
        return 1;
    }
    
    // TODO: Process command line arguments.
    char file_permissions[11];
    int total, size, usrarg;
    int num_file = 0;
    scanf("%s %d",file_permissions, &total);
    while (scanf("%s %*s %*s %*s %d %*s %*s %*s %*s",file_permissions, &size) == 2){
 
        if (*file_permissions == '-') {
            usrarg = strtol(argv[1],NULL,10);
            if (argv[2] == NULL) {
                if (size > usrarg) {
                    num_file += 1;
                }
            }else{
                if (size > usrarg && check_permissions(file_permissions+1, argv[2]) == 0) {
                    num_file += 1;
                }
            }
        }
    }
    
    printf("%d\n", num_file);
    // TODO Call check permissions and then print the returned value.

    return 0;
}

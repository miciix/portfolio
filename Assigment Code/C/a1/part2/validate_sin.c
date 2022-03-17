#include <stdio.h>
#include <stdlib.h>

int populate_array(int sin, int *sin_array);
int check_sin(int *sin_array);


int main(int argc, char **argv) {
    // TODO: Verify that command line arguments are valid.
    if (!(argc == 2)) {
        return 1;
    }
    // TODO: Parse arguments and then call the two helpers in sin_helpers.c
    // to verify the SIN given as a command line argument.
    int sin;
    int sin_array[9];
    sin = strtol(argv[1],NULL,10);
    populate_array(sin, sin_array);
    if(check_sin(sin_array) == 1){
        printf("Invalid SIN\n");
    }else{
        printf("Valid SIN\n");
    }
    return 0;
}

// TODO: Implement populate_array
/*
 * Convert a 9 digit int to a 9 element int array.
 */
int populate_array(int sin, int *sin_array) {
    int num_digit = 0;
    int cpy_num = sin;
    while(cpy_num != 0)
    {
        cpy_num /= 10;
        num_digit += 1;
    }
    if (num_digit == 9){
        for(int i = 8 ; i >= 0 ; i--){
            *(sin_array + i) = sin % 10;
            sin /= 10;
        }
    }else{
        return 1;
    }
    return 0;
}

// TODO: Implement check_sin
/* 
 * Return 0 (true) iff the given sin_array is a valid SIN.
 */
int check_sin(int *sin_array) {
    int number = 121212121;
    int num_toadd;
    int num_final = 0;
    if (*sin_array == 0) {
        return 1;
    }
    for (int i = 8; i >= 0; i--) {
        num_toadd = (*(sin_array+i)) * (number % 10);
        if (num_toadd > 10) {
            num_toadd = num_toadd % 10 + (num_toadd / 10) % 10;
        }
        number /= 10;
        num_final += num_toadd;
    }
    
    if (num_final % 10 != 0) {
        return 1;
    }
    
    return 0;
}

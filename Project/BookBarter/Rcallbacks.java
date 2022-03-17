package com.csc301.students.BookBarter;

import android.support.annotation.NonNull;

public interface Rcallbacks {
    void onSuccess(@NonNull String value);

    void onError(@NonNull Throwable throwable);


}

package com.csc301.students.BookBarter;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SuccessfulPost extends AppCompatActivity {
    private Button bt1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successful_post);

        getWindow().getDecorView().
                setSystemUiVisibility(0);
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),
                R.color.colorPrimary));

        bt1 = findViewById(R.id.backbut);

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backmain();

            }
        });


    }


    public void backmain() {
        Intent intent = new Intent(this, Mainpage.class);
        startActivity(intent);
        //destroy SuccessfulLogin Page after user clicked back to main button
        SuccessfulPost.this.finish();

    }
}



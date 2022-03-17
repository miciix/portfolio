package com.csc301.students.BookBarter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Verifycode extends AppCompatActivity {
    private Button verify,back;
    private EditText code;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifycode);

        getWindow().getDecorView().
                setSystemUiVisibility(0);
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),
                R.color.colorPrimary));

        verify = findViewById(R.id.button1);
        back = findViewById(R.id.button2);
        back.setPaintFlags(back.getPaintFlags() |
                Paint.UNDERLINE_TEXT_FLAG);
        code = findViewById(R.id.et1);
        final Intent intent = new Intent(this, SuccessfulLogin.class);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Verifycode.this,Register.class);
                startActivity(intent);
                //destroy Register Page after successful registered
                Verifycode.this.finish();
            }
        });


        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(Verifycode.this,SuccessfulLogin.class);

                //check verification code


                String vercode = code.getText().toString();
                /*TOdo:sent verification request to server and get token save it in share data
                */
                final String username,password,email,token;

                SharedPreferences sp = getSharedPreferences("RegToken", 0);
                username = sp.getString("username",null);
                password = sp.getString("password",null);
                email = sp.getString("email",null);
                token = sp.getString("token",null);
                final String baeartoken = "Bearer "+token;



                Call<ResponseBody> call = RetrofitClient.getInstance()
                        .getApi()
                        .register(baeartoken,username,email,password,vercode);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code()==200){
                            try {
                                String responsetext = response.body().string();
                                if(responsetext.contains("\"success\":true")){
                                    startActivity(intent);




                                }else{

                                 Toast.makeText(Verifycode.this,"Wrong active code", Toast.LENGTH_LONG).show();


                                }

                            } catch ( IOException e ) {
                                Toast.makeText(Verifycode.this,"fail to get response body string", Toast.LENGTH_LONG).show();
                                e.printStackTrace();

                            }


                        }else{

                            Toast.makeText(Verifycode.this, "Error with error code"+response.code()+"with username"+username+"and email "+email+"beartoken: "+baeartoken+"token"+token, Toast.LENGTH_LONG).show();


                        }



                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {





                    }
                });
























            }
        });











    }
}

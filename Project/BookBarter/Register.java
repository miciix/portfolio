package com.csc301.students.BookBarter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Register extends AppCompatActivity {
    private Button regbut;
    private EditText username, password, re_password, email;
    private String usern, pass, re_pass, em;
    public static int flag = 0;
    private static String s;
    Rcallbacks RCB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getWindow().getDecorView().
                setSystemUiVisibility(0);
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),
                R.color.colorPrimary));

        //sign up button and text editor
        regbut = findViewById(R.id.Register2);
        username = findViewById(R.id.etname);
        password = findViewById(R.id.etpass);
        re_password = findViewById(R.id.etrepass);
        email = findViewById(R.id.emailet);

        //Action listener
        regbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();


            }
        });
    }


    public void register() {

        usern = username.getText().toString();
        pass = password.getText().toString();
        re_pass = re_password.getText().toString();
        em = email.getText().toString();
        final Intent intent = new Intent(this, Verifycode.class);


        if (usern.length() != 0 && pass.length() != 0) {
            if (pass.equals(re_pass)) {


                Call<ResponseBody> call = RetrofitClient.getInstance()
                        .getApi()
                        .getActive(em);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                        s = response.body().toString();

                        String s1 = response.message();
                        ResponseBody res = response.body();
                        String s2 = response.toString();
                        try {
                            String s3 = res.string();
                            JSONObject obj = new JSONObject(s3);
                            Toast.makeText(getApplicationContext(), s3,
                                    Toast.LENGTH_SHORT).show();
                            if (s3.contains("\"success\":true")) {
                                /*ToDO: save register name and email to share data for verification
                                *
                                 */


                                SharedPreferences sp = getSharedPreferences("RegToken", 0);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("email",em);
                                editor.putString("username",usern);
                                editor.putString("password",pass);
                                editor.putString("token",obj.optString("token"));
                                editor.commit();
                                Toast.makeText(Register.this, obj.optString("token"), Toast.LENGTH_LONG).show();








                                Toast.makeText(getApplicationContext(), "Register Successful",
                                        Toast.LENGTH_SHORT).show();



                                //int flag = active(em);


                                startActivity(intent);
                                //destroy Register Page after successful registered
//                                Register.this.finish();


                            } else {
                                Toast.makeText(getApplicationContext(), "Email Already Exists",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(Register.this, "io fail", Toast.LENGTH_LONG).show();
                        } catch ( JSONException e ) {
                            e.printStackTrace();
                            Toast.makeText(Register.this, "jsonfail", Toast.LENGTH_LONG).show();
                        }


                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(Register.this, "fail to connect", Toast.LENGTH_LONG).show();
                        Toast.makeText(Register.this, t.getMessage(), Toast.LENGTH_LONG).show();


                    }
                });


            } else {
                Toast.makeText(getApplicationContext(), "Passwords do not Match",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Username or Password Cannot be Empty",
                    Toast.LENGTH_SHORT).show();

        }


    }

//    int active(String em){
//        //TOdo: sent active request and save the token in the jason object
//        Call<ResponseBody> call2 = RetrofitClient.getInstance()
//                .getApi()
//                .getActive(em);
//        call2.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response2) {
//                if(response2.code()==200){
//                    Toast.makeText(Register.this,"success call", Toast.LENGTH_LONG).show();
//
//                }else{
//                    Toast.makeText(Register.this,"fail call", Toast.LENGTH_LONG).show();
//
//                }

                // JsonResult res  = response2.body();

//                                        SharedPreferences sp = getSharedPreferences("ActiveToken", 0);
//                                        SharedPreferences.Editor editor_active = sp.edit();
//                                        editor_active.putString("email",em);
//                                        editor_active.putString("token",res.getToken());
//                                        editor_active.commit();
//
//
//                                        sp = getSharedPreferences("ActiveToken", 0);
//                                        Toast.makeText(Register.this, sp.getString("token",null), Toast.LENGTH_LONG).show();

//                String result = response2.body().toString();
//
//                Toast.makeText(Register.this,result, Toast.LENGTH_LONG).show();







//
//
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Toast.makeText(Register.this, "Error:fail to active", Toast.LENGTH_LONG).show();
//
//            }
//        });




//
//        return 0;
//
//
//    }


}


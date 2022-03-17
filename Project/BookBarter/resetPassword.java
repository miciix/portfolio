package com.csc301.students.BookBarter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.text.method.PasswordTransformationMethod;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class resetPassword extends AppCompatActivity {
    private Button getcodeB,resetB;
    private EditText  password, re_password, email,activeET;
    private String  pass, re_pass, em,active;
    Rcallbacks RCB;
    private String token;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        getWindow().getDecorView().
                setSystemUiVisibility(0);
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),
                R.color.colorPrimary));

        ini();
        iniListener();


    }
    private void startTimer(){
        final long time=60*1000;
        final long interval=1000;
        CountDownTimer timer=new CountDownTimer(time,interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                getcodeB.setEnabled(false);
                long timeleft =millisUntilFinished/1000;
                getcodeB.setText(String.format("Please Wait %02d:%02d",timeleft/60,timeleft%60));
            }

            @Override
            public void onFinish() {
                getcodeB.setEnabled(true);
                getcodeB.setText("send verification code again");
            }
        }.start();
    }
    private void ini(){
        //sign up button and text editor
        getcodeB=findViewById(R.id.getVcode);
        getcodeB.setPaintFlags(getcodeB.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        resetB=findViewById(R.id.resetP);
        password = findViewById(R.id.etpass);
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        re_password = findViewById(R.id.etrepass);
        re_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        email = findViewById(R.id.emailet);
        activeET=findViewById(R.id.vCode);
        resetB.setEnabled(false);
        SharedPreferences sp = getSharedPreferences("RegToken", 0);
        String emailString = sp.getString("email",null);
        if(emailString!=null){
            email.setText(emailString);
        }

    }
    private void iniListener(){
        //get code button listener
        getcodeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCode();

            }
        });
        //reset password listener

        resetB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkEditor()) resetPass();


            }
        });
    }
    private boolean checkEditor(){
        active=activeET.getText().toString();
        pass=password.getText().toString();
        re_pass=re_password.getText().toString();
        android.util.Log.d("strToIntTest", "password=" + pass
                +"\t repassword="+re_pass +"\tcheck="+re_pass.equals(pass));
        if (active.isEmpty()){
            Toast.makeText(getApplicationContext(), "Verification Code Cannot be Empty",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if(pass.isEmpty()){
            Toast.makeText(getApplicationContext(), "Password Cannot be Empty",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if(re_pass.isEmpty()){
            Toast.makeText(getApplicationContext(), "Confirm Password Cannot be Empty",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!re_pass.equals(pass)){
            Toast.makeText(getApplicationContext(), "Passwords are not the Same",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void resetPass(){

            JsonObject body = new JsonObject();
            body.addProperty("password", pass);
            body.addProperty("active", active);
            Call<JsonResult> call = RetrofitClient.getInstance()
                    .getApi()
                    .resetPassword("Bearer " + token, body);
            call.enqueue(new Callback<JsonResult>() {
                @Override
                public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {

                    JsonResult res = response.body();

                    if (Integer.toString(response.code()).equals("200")) {
                        Toast.makeText(getApplicationContext(),
                                "Success", Toast.LENGTH_SHORT).show();
                        goback();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    }


                }

                @Override
                public void onFailure(Call<JsonResult> call, Throwable t) {
                    t.printStackTrace();
                }
            });


    }
    private void goback(){
        SharedPreferences sp = getSharedPreferences("loginToken", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
        //go from mainpage to loginpage
        Intent intent = new Intent(resetPassword.this,
                MainActivity.class);
        startActivity(intent);
        //destroy mainpage
        resetPassword.this.finish();

    }
    private void getCode(){

        em = email.getText().toString();
        if (!em.isEmpty()){
            checkUser(em);
            resetB.setEnabled(true);
            startTimer();
        }
        else{
            Toast.makeText(getApplicationContext(), "Please Enter Email First",
                    Toast.LENGTH_SHORT).show();
        }
    }
    private void checkUser(String em) {

        Call<JsonResult> call = RetrofitClient.getInstance()
                .getApi()
                .checkUser(em);
        final String cEmail=em;
        android.util.Log.d("strToIntTest", "email=" +em);
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {

                JsonResult res = response.body();
                android.util.Log.d("strToIntTest", "body=" +response.body());
                //succeed="succeed";
                if (res.getSuccess().equals("true")){
                    android.util.Log.d("strToIntTest", "success=" +res.getSuccess());
                    sendVCode(cEmail);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Email is not registered",
                            Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                t.printStackTrace();
                //tv.setText("email is not found please register first");


            }
        });
    }
    private void sendVCode(String email){
        Call<ResponseBody> call = RetrofitClient.getInstance()
                .getApi()
                .getActive(email);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                ResponseBody res = response.body();
                try{
                    String JString = res.string();
                    JSONObject obj = new JSONObject(JString);
                    token=obj.optString("token");
                    android.util.Log.d("strToIntTest", "success=" +"token");
                } catch (IOException e) {
                    e.printStackTrace();

                } catch ( JSONException e ) {
                    e.printStackTrace();

                }



            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {



            }
        }

                );
    }
}




















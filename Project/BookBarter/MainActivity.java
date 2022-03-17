package com.csc301.students.BookBarter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private Button button1, button2, resetPassbutton;
    private String username, password;
    private EditText userET, passET;
    private String pass, usr;
    private static final String FILE_NAME="token.txt";
    private String rememberMe;
    private CheckBox rembMSwitch;

    Token token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);


        getWindow().getDecorView().
                setSystemUiVisibility(0);
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),
                R.color.colorPrimary));

        button1 = findViewById(R.id.RegBut);
        button2 = findViewById(R.id.LogBut);
        userET = findViewById(R.id.PassTx);
        userET.setTransformationMethod(PasswordTransformationMethod.getInstance());
        passET = findViewById(R.id.UserTx);
        rembMSwitch= findViewById(R.id.rembMe2);
        resetPassbutton=findViewById(R.id.fpassword);
        //get the Data Object
        token = (Token) getApplication();

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegister();

            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();

            }
        });

        resetPassbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openResetPassword();

            }
        });
        resetPassbutton.setPaintFlags(resetPassbutton.getPaintFlags() |
                Paint.UNDERLINE_TEXT_FLAG);

        rembMSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    rememberMe="on";

                }else {
                    rememberMe="off";
                }
            }
        });

        SharedPreferences sp = getSharedPreferences("RegToken", 0);
        String email = sp.getString("email",null);
        if(email!=null){
            passET.setText(email);
        }
        SharedPreferences splog = getSharedPreferences("loginToken", 0);
        String stayLoginB=splog.getString("stayLogin","off");
        //android.util.Log.d("strToIntTest", "stayLogin=" + stayLoginB);
        //android.util.Log.d("strToIntTest", "loginToken=" +sp.getString("token",null));
        if  (stayLoginB.equals("on")){
            android.util.Log.d("strToIntTest", "stayLogin=" + stayLoginB);
            stayLogin();
        }





    }
    public void stayLogin(){
        final Intent intent = new Intent(this, Mainpage.class);
        SharedPreferences loginSp = getSharedPreferences("loginToken", 0);
        final String loginToken=loginSp.getString("token",null);

        Call<JsonResult> call = RetrofitClient.getInstance().getApi().getUserInfo("Bearer " + loginToken);
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {

                JsonResult res = response.body();
                if(res.getSuccess()=="true"){
                    token.setToken(loginToken);
                    startActivity(intent);
                    MainActivity.this.finish();}

                else{
                    Toast.makeText(MainActivity.this, "Login Expired!",
                            Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Login Expired!",
                        Toast.LENGTH_SHORT).show();
                t.printStackTrace();


            }
        });


    }

    public void openRegister() {
       Intent intent = new Intent(this, Register.class);
        startActivity(intent);

    }
    public void openResetPassword() {
        Intent intent = new Intent(this, resetPassword.class);
        startActivity(intent);

    }

    public void login() {
        username = userET.getText().toString();
        password = passET.getText().toString();
        final Intent intentlog = new Intent(this, Mainpage.class);
        Call<JsonResult> call = RetrofitClient.getInstance()
                .getApi()
                .login(username, password);


        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                if (response.code() == 200) {

                    JsonResult res = response.body();
                    try {

                        //get response and log in successful
                        if (res.getSuccess().equals("true")) {
                            //get token
                            token.setToken(res.getToken());
                            //open the new intent(main page in this case)

                         //Get token from response and store it in share data




                            //get token and save the token in the properties file
                            try {

                                // token been saved in the in obj.optString

                                //Toast.makeText(MainActivity.this, obj.optString("token"), Toast.LENGTH_LONG).show()




                                //save token in th FILE_NAME files
                                //FileOutputStream fos = null;



//                                try{
//                                    fos = openFileOutput(FILE_NAME,MODE_PRIVATE);
//                                    fos.write(res.getToken().getBytes());
//                                    Toast.makeText(MainActivity.this,"data"+res.getToken()+"Saved to"+getFilesDir()+"/"+FILE_NAME, Toast.LENGTH_LONG).show();
//                                }
//                                catch ( FileNotFoundException e ){
//                                    Toast.makeText(MainActivity.this,"FileNotFound", Toast.LENGTH_LONG).show();
//                                    e.printStackTrace();
//                                }catch(IOException e){
//                                    Toast.makeText(MainActivity.this,"IOException 1.0", Toast.LENGTH_LONG).show();
//                                    e.printStackTrace();
//                                }finally {
//                                    if(fos != null){
//                                        fos.close();
//                                    }
//                                }

                                SharedPreferences sp = getSharedPreferences("loginToken", 0);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("email",password);
                                editor.putString("token",res.getToken());
                                editor.putString("stayLogin",rememberMe);
                                android.util.Log.d("strToIntTest", ",remembermelogin=" +rememberMe);
                                editor.commit();
                                //android.util.Log.d("strToIntTest", ",remembermeintoken=" +sp.getString("stayLogin","off"));
                                //Toast.makeText(MainActivity.this,"Welcome !", Toast.LENGTH_LONG).show();


                            }catch ( Exception e ){
                                e.printStackTrace();
                                Toast.makeText(MainActivity.this,"fail to load jason object", Toast.LENGTH_LONG).show();
                            }















                            startActivity(intentlog);
                            //destroy MainActivity Page after Logged in
                            MainActivity.this.finish();
                        } else {
                            //if not login successful
                            Toast.makeText(MainActivity.this,
                                    "Email or Passowrd is Incorrect",
                                    Toast.LENGTH_SHORT).show();

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    Toast.makeText(MainActivity.this,
                            "Email or Password is Incorrect",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {

                Toast.makeText(MainActivity.this,
                        "Email or Password is Incorrect", Toast.LENGTH_SHORT).show();


            }
        });


    }


}


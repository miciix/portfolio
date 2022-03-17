package com.csc301.students.BookBarter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.allen.library.SuperTextView;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Contactseller extends AppCompatActivity {
    private Button post_bt,clean_bt;
    private EditText message;
    String Message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactseller);

        post_bt = findViewById(R.id.postbt);

        clean_bt = findViewById(R.id.cleanbt);
        message = findViewById(R.id.Message);
        final Intent AdsViewPage = new Intent(this,Mainpage.class);
        SharedPreferences sp = getSharedPreferences("SellerInfo", 0);
        final String seller_em = sp.getString("seller_em",null);




        post_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //Todo:post messages to database and sent it to seller
            //Set up api for the call
                SharedPreferences sp = getSharedPreferences("loginToken", 0);
                String token = sp.getString("token",null);
                String buyer_em = sp.getString("email",null);
                String msg = message.getText().toString();

                //Toast.makeText(Contactseller.this, "buyer_em:"+buyer_em
                      //  +"seller_em:"+seller_em+"MSG:"+msg, Toast.LENGTH_LONG).show();

                Call<ResponseBody> call = RetrofitClient.getInstance().getApi()
                        .sendmsg("Bearer "+token,seller_em,buyer_em,msg);
                //Actual call

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code() == 200){
                            ResponseBody res = response.body();
                            String s3 = "";
                            try {
                                s3  = res.string();
                            } catch ( IOException e ) {
                                e.printStackTrace();
                            }

                            if (s3.contains("\"success\":true")) {
                            //successfully communicate with server and has valid call back
                                startActivity(AdsViewPage);
                                Contactseller.this.finish();


                               // Toast.makeText(Contactseller.this,response.toString(), Toast.LENGTH_LONG).show();



                            }


                        }else{
                            Toast.makeText(Contactseller.this, response.code(), Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(Contactseller.this, "connection lost", Toast.LENGTH_LONG).show();




                    }
                });











            }
        });


        clean_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //Todo:Clean messages just in case user want to re-write message
            message.setText("");








            }
        });









    }
}

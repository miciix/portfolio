package com.csc301.students.BookBarter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.csc301.students.BookBarter.SearchAds.Data;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class getNotice extends AppCompatActivity {
    Token token;
    private Data post;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_notice);
        SharedPreferences splog = getSharedPreferences("loginToken", 0);
        String stayLoginB=splog.getString("stayLogin","off");
        token = (Token) getApplication();
        //android.util.Log.d("strToIntTest", "stayLogin=" + stayLoginB);
        //android.util.Log.d("strToIntTest", "loginToken=" +sp.getString("token",null));
        if  (stayLoginB.equals("on")){
            android.util.Log.d("strToIntTest", "stayLogin=" + stayLoginB);
            stayLogin();
        }
        else{
            Toast.makeText(getNotice.this,"Please login first", Toast.LENGTH_LONG).show();
            backToMain();
        }

        
    }
    private void backToMain(){

        final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void goToView(){
        Intent intent = getIntent();
        post = (Data) intent.getSerializableExtra("data");
        if (post==null){
            backToMain();
            finish();
        }
        else{
            android.util.Log.d("strToIntTest", "postEmail=" +post.getEmail()
            +" postID =" + post.getId());
            Intent newintent = new Intent(this,
                    AdsViewPage.class);
            newintent.putExtra("data", post);

            startActivity( newintent);
            finish();
        }

    }
    private void stayLogin() {

        SharedPreferences loginSp = getSharedPreferences("loginToken", 0);
        final String loginToken=loginSp.getString("token",null);

        Call<JsonResult> call = RetrofitClient.getInstance().getApi().getUserInfo("Bearer " + loginToken);
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {

                JsonResult res = response.body();
                if(res.getSuccess()=="true"){
                    token.setToken(loginToken);
                    goToView();
                    }
                else{
                    backToMain();
                }

            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                t.printStackTrace();


            }
        });
    }

}

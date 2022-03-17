package com.csc301.students.BookBarter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.allen.library.SuperTextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellerInformation extends AppCompatActivity {
    private SuperTextView yearofstudy_STV;
    private SuperTextView username_STV;
    private SuperTextView campus_STV;
    private SuperTextView email_STV;
    private Intent intent;
    private Token token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sellerinformation);


        Toolbar toolbar = (Toolbar) findViewById(R.id.seller_toolbar);
        //create toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //create a back icon on toolbar
        if (getSupportActionBar() != null) {
            // Enable the Up button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        campus_STV = (SuperTextView) findViewById(R.id.campus);
        yearofstudy_STV = (SuperTextView) findViewById(R.id.year_of_study);
        username_STV = (SuperTextView) findViewById(R.id.user_name);
        email_STV = (SuperTextView) findViewById(R.id.email);

        final Intent AdsViewPage = new Intent(this,Mainpage.class);
        SharedPreferences sp = getSharedPreferences("SellerInfo", 0);
        final String seller_em = sp.getString("seller_em",null);
        Log.d("Seller Response",seller_em.toString());
        token = (Token) getApplication();

        //listener for back on toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//返回
            }
        });

        //get all data

        Call<JsonResult> call = RetrofitClient.getInstance().getApi().getSellerInfo("Bearer " + token.getToken(), seller_em);
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {

                JsonResult res = response.body();
                if (res.getCampus() != null) {
                    campus_STV.setRightString(res.getCampus());
                }
                if (res.getUsername() != null) {
                    username_STV.setRightString(res.getUsername());
                }
                if (res.getYearOfStudy() != null) {
                    yearofstudy_STV.setRightString(res.getYearOfStudy());
                }
                if (res.getEmail() != null){
                    email_STV.setRightString(res.getEmail());
                }

            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}

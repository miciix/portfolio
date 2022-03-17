package com.csc301.students.BookBarter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.allen.library.SuperTextView;
import com.csc301.students.BookBarter.SearchAds.Data;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdsViewPage extends AppCompatActivity {
    private SuperTextView title_STV;
    private SuperTextView email_STV;
    private SuperTextView price_STV;
    private SuperTextView description_STV;

    private Button contact_bt;
    private Button seller_info_bt;

    private CheckBox interested;
    private Data post;
    private Token token;
    boolean isChecked = false;
    private JsonArray interested_lst = new JsonArray();
    private ImageView textbookView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        token = (Token) getApplication();
        setContentView(R.layout.activity_ads_view_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.adsview_toolbar);
        //create toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //create a back icon on toolbar
        if (getSupportActionBar() != null) {
            // Enable the Up button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //listener for back on toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//back
            }
        });
        textbookView = findViewById(R.id.textbookPicture);
        contact_bt = findViewById(R.id.contact_id);
        seller_info_bt = findViewById(R.id.seller_info);
        email_STV = (SuperTextView) findViewById(R.id.ads_email);
        title_STV = (SuperTextView) findViewById(R.id.ads_title);
        price_STV = (SuperTextView) findViewById(R.id.ads_price);
        description_STV = (SuperTextView) findViewById(R.id.ads_description);
        //get data
        Intent intent = getIntent();
        post = (Data) intent.getSerializableExtra("data");
        //set data


//HEAD:BookBarter/app/src/main/java/com/csc301/students/BookBarter/AdsViewPage.java
        email_STV.setLeftString("Email: " + post.getEmail());
        title_STV.setLeftString("Title: "+ post.getTitle());
        price_STV.setLeftString("Price: " + post.getPrice());
        if(post.getImage()!=null){
            showImage(post.getImage());
        }

/*
        email_STV.setRightString(post.getEmail());
        title_STV.setRightString(post.getTitle());
        price_STV.setRightString(post.getPrice());
*/
        //description_STV.setCenterTopString(post.getDescription());
        final String seller_Em = post.getEmail();


        contact_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contactsell(seller_Em);}
        });
        seller_info_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sellerinfo(seller_Em);}
        });




        description_STV.setLeftString("Description: "+ post.getDescription());

        interested = (CheckBox) findViewById(R.id.ads_interested);
        getData();
        interested.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //do something
                    getData();
                    if (!interested_lst.contains(new JsonPrimitive(post.getId()))) {
                        interested_lst.add(post.getId());
                    }
                    sendData();
                } else {
                    //do something else
                    if (interested_lst.contains(new JsonPrimitive(post.getId()))) {
                        interested_lst.remove(new JsonPrimitive(post.getId()));
                    }
                    sendData();
                }
            }
        });
        interested.setChecked(isChecked);
    }

    public void getData() {
        Log.d("HAHA", "getting data");
        Call<JsonResult> call = RetrofitClient.getInstance().getApi().getInterested("Bearer " + token.getToken());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {

                JsonResult res = response.body();
                if (res.getInterested() != null) {
                    interested_lst = res.getInterested();
                    if (interested_lst.contains(new JsonPrimitive(post.getId()))) {
                        interested.setChecked(true);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void sendData() {
        JsonObject json = new JsonObject();
        json.add("interested", interested_lst);
        Log.d("HAHA", "onResponse: " + interested_lst);

        Call<JsonResult> call = RetrofitClient.getInstance()
                .getApi()
                .putInterested("Bearer " + token.getToken(), json);
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {

                JsonResult res = response.body();

                if (res.getSuccess().equals("true")) {
                    /*
                    Toast.makeText(AdsViewPage.this,
                            "Success", Toast.LENGTH_SHORT).show();*/
                } else {
                    Toast.makeText(AdsViewPage.this,
                            "Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                t.printStackTrace();

            }
        });
    }
    public void Contactsell(String seller_em){
        Intent contact = new Intent(this, Contactseller.class);

        SharedPreferences sp = getSharedPreferences("SellerInfo", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("seller_em",seller_em);
        editor.commit();
        startActivity(contact);

    }

    public void Sellerinfo(String seller_em){
        Intent contact = new Intent(this, SellerInformation.class);

        SharedPreferences sp = getSharedPreferences("SellerInfo", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("seller_em", seller_em);
        editor.commit();
        startActivity(contact);

    }
    private void showImage(String url){
        int width  = Resources.getSystem().getDisplayMetrics().widthPixels;
        String clhttp="https://res.cloudinary.com/hyoenehbs/image/upload";

        int strLen=clhttp.length();

        String newUrl=clhttp+"/c_scale,w_"+ width +url.substring(strLen-1);
        Log.d("HAHA", "tryurl: " + newUrl);
        Picasso
                .with(AdsViewPage.this)
                .load(newUrl)
                .into(textbookView);
    }



}

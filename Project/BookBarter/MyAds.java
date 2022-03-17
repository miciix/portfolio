package com.csc301.students.BookBarter;

import android.content.Intent;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.Gravity.CENTER_VERTICAL;
import static android.widget.LinearLayout.VERTICAL;

public class MyAds extends AppCompatActivity {
    protected ScrollView adsScroll;
    private Token token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ads);


        token = (Token) getApplication();

        adsScroll = findViewById(R.id.myadsscroll);
        requestAds();

    }

    private void requestAds(){
        Call<JsonResult> call = RetrofitClient.getInstance().getApi().myposts("Bearer " + token.getToken());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                LinearLayout layout = new LinearLayout(getApplicationContext());
                layout.setOrientation(VERTICAL);
                layout.setGravity(CENTER_VERTICAL); 



                JsonResult res = response.body();
                String[] titles = res.getTitles();
                String[] ids = res.getIds();
                Button[] button = new Button[titles.length];
                for(int i = 0; i < titles.length; i++){
                    button[i] = new Button(getApplicationContext());
                    button[i].setText(titles[i]);
                    button[i].setTag(ids[i]);

                    button[i].setWidth(334);
                    button[i].setTextColor(ContextCompat.getColor(getApplicationContext(),
                            R.color.colorPrimary));
                    button[i].setBackgroundResource(0);
                    button[i].setPaddingRelative(50,50,50,50);
                    button[i].setPaintFlags(button[i].getPaintFlags() |
                            Paint.UNDERLINE_TEXT_FLAG);

                    button[i].setOnClickListener(new MyAds.adButtonListener());
                    layout.addView(button[i]);
                }
                adsScroll.addView(layout);
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    class adButtonListener implements View.OnClickListener{
        public void onClick(View v) {
            String id = (String)v.getTag();
            Intent intent = new Intent(MyAds.this,
                    EditAd.class);
            intent.putExtra("id", id);
            startActivity(intent);
            MyAds.this.finish();
        }

    }

    class backButtonListener implements View.OnClickListener{
        public void onClick(View v) {
            Intent intent = new Intent(MyAds.this,
                    Mainpage.class);
            startActivity(intent);
            MyAds.this.finish();
        }


    }
}


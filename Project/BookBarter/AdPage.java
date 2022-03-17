package com.csc301.students.BookBarter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdPage extends AppCompatActivity {
    private Button menuButton, postAdButton;
    private ImageButton image;
    private EditText titleText, descriptionText, priceText;
    private String title, description, price;
    private Bitmap imageBitmap;
    private Token token;
    private static final int RESULT_LOAD_IMAGE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_create);

        postAdButton = findViewById(R.id.postad);
        menuButton = findViewById(R.id.menu);
        postAdButton.setOnClickListener(new AdPage.postListener());
        menuButton.setOnClickListener(new AdPage.menuListener());
        titleText = findViewById(R.id.title);
        descriptionText = findViewById(R.id.description);
        priceText = findViewById(R.id.price);
        image = findViewById(R.id.image);
        image.setOnClickListener(new AdPage.imageListener());

        token = (Token) getApplication();
    }

    class postListener implements View.OnClickListener {
        public void onClick(View v) {

            token = (Token) getApplication();
            title = titleText.getText().toString();
            description = descriptionText.getText().toString();
            price = priceText.getText().toString();
            imageBitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();

            if (title.isEmpty() || description.isEmpty() || price.isEmpty()){
                Toast.makeText(AdPage.this, "You must fill in all fields!", Toast.LENGTH_SHORT).show();

            } else if (imageBitmap == null) {
                Toast.makeText(AdPage.this, "You have to upload an image!", Toast.LENGTH_SHORT).show();
            } else{

                Call<ResponseBody> call = RetrofitClient.getInstance()
                        .getApi()
                        .post("Bearer " + token.getToken(), title, description, price,
                                imageBitmap);


                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        String s = response.body().toString();

                        String s1 = response.message();
                        ResponseBody res = response.body();
                        String s2 = response.toString();
                        try {
                            String s3 = res.string();
                            if (s3.contains("\"success\":true")) {
                                //image.setImageBitmap(imageBitmap);
                                Intent intent = new Intent(AdPage.this,
                                        SuccessfulPost.class);
                                startActivity(intent);
                                //destroy Ad Page after successful post
                                AdPage.this.finish();
                                Toast.makeText(AdPage.this, "Ad was successfully posted!", Toast.LENGTH_SHORT).show();


                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(AdPage.this, "fail to connect", Toast.LENGTH_LONG).show();
                        Toast.makeText(AdPage.this, t.getMessage(), Toast.LENGTH_LONG).show();


                    }
                });
            }
        }

    }

    class imageListener implements View.OnClickListener {
        public void onClick(View v) {
            Intent photoGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(photoGallery, RESULT_LOAD_IMAGE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK){
            Uri selectedImage = data.getData();
            image.setImageURI(selectedImage);
        }

    }

    class menuListener implements View.OnClickListener {
        public void onClick(View v) {

            Intent intent = new Intent(AdPage.this,
                    Mainpage.class);
            startActivity(intent);

            AdPage.this.finish();
        }

    }
}

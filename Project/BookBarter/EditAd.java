package com.csc301.students.BookBarter;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.squareup.picasso.Picasso;

public class EditAd extends AppCompatActivity {
    private Button backButton, editAdButton, deleteAdButton;
    private ImageButton image;
    private EditText descriptionText, priceText;
    private String description, price;
    private Bitmap imageBitmap;
    private ImageView textbookPicture;
    private Token token;
    private static final int RESULT_LOAD_IMAGE = 1;
    private Intent intent;
    private String buttonId;
    private File file;
    private Boolean imageButtonSet;
    private TextView book_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ad);


        deleteAdButton = findViewById(R.id.delete_ad);
        deleteAdButton.setOnClickListener(new EditAd.deleteListener());
        editAdButton = findViewById(R.id.editad);
        backButton = findViewById(R.id.myads);
        editAdButton.setOnClickListener(new EditAd.editListener());
        backButton.setOnClickListener(new EditAd.backListener());
        book_name = findViewById(R.id.book_name);
        descriptionText = findViewById(R.id.editDescription);
        priceText = findViewById(R.id.editPrice);
        image = findViewById(R.id.editImage);
        image.setOnClickListener(new EditAd.imageListener());
        textbookPicture = findViewById(R.id.textbookPicture);
        imageButtonSet = false;


        token = (Token) getApplication();

        intent = getIntent();
        buttonId = intent.getStringExtra("id");

        Call<JsonResult> call = RetrofitClient.getInstance()
                .getApi()
                .getAd("Bearer " + token.getToken(), buttonId);

        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult res = response.body();
                String[] ad = res.getAdFields();
                book_name.setText(ad[0]);
                descriptionText.setText(ad[1]);
                priceText.setText(ad[2]);
                showImage(ad[3]);

            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                Toast.makeText(EditAd.this, "fail to connect", Toast.LENGTH_LONG).show();
                Toast.makeText(EditAd.this, t.getMessage(), Toast.LENGTH_LONG).show();


            }
        });

    }

    class editListener implements View.OnClickListener {
        public void onClick(View v) {

            token = (Token) getApplication();
            description = descriptionText.getText().toString();
            price = priceText.getText().toString();
            if(imageButtonSet) {
                imageBitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
            } else {
                imageBitmap = ((BitmapDrawable) textbookPicture.getDrawable()).getBitmap();
            }
            if (description.isEmpty() || price.isEmpty()){
                Toast.makeText(EditAd.this, "You must fill in all fields!", Toast.LENGTH_SHORT).show();

            } else if (imageBitmap == null) {
                Toast.makeText(EditAd.this, "You have to upload an image!", Toast.LENGTH_SHORT).show();
            } else{

                formatFile();
                RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                MultipartBody.Builder builder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM);
                builder.addFormDataPart("upload", file.getName(), imageBody);
                builder.addFormDataPart("description", description);
                builder.addFormDataPart("id", buttonId);
                builder.addFormDataPart("price", price);
                builder.addFormDataPart("title", (String)book_name.getText());
                List<MultipartBody.Part> body = builder.build().parts();

                Call<JsonResult> call = RetrofitClient.getInstance()
                        .getApi()
                        .updatePost("Bearer " + token.getToken(), body);

                call.enqueue(new Callback<JsonResult>() {
                    @Override
                    public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                        Log.d("calling", "calling the database");
                        JsonResult res = response.body();
                        if(res.getSuccess().equals("true")){
                            Log.d("success", "edit ads successful");
                            Toast.makeText(EditAd.this,"Post successful ", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(EditAd.this, res.getSuccess(), Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<JsonResult> call, Throwable t) {
                        Log.d("failed", "failed to call database");
                        Toast.makeText(EditAd.this, "fail to connect", Toast.LENGTH_LONG).show();
                        Toast.makeText(EditAd.this, t.getMessage(), Toast.LENGTH_LONG).show();


                    }

                });
                Intent intent = new Intent(EditAd.this,
                        MyAds.class);
                startActivity(intent);

                EditAd.this.finish();
            }
        }

    }

    class deleteListener implements  View.OnClickListener{
        public void onClick(View v) {
            token = (Token) getApplication();

            Call<JsonResult> call = RetrofitClient.getInstance()
                    .getApi()
                    .deleteAd("Bearer " + token.getToken(), buttonId);

            call.enqueue(new Callback<JsonResult>() {
                @Override
                public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                    Log.d("calling", "calling the database");
                    JsonResult res = response.body();
                    if(res.getSuccess().equals("true")){
                        Log.d("success", "ad deleted successfully");
                        Toast.makeText(EditAd.this,"Ad successfully Deleted", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(EditAd.this, res.getSuccess(), Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<JsonResult> call, Throwable t) {
                    Log.d("failed", "failed to call database");
                    Toast.makeText(EditAd.this, "fail to connect", Toast.LENGTH_LONG).show();
                    Toast.makeText(EditAd.this, t.getMessage(), Toast.LENGTH_LONG).show();


                }

            });

            Intent intent = new Intent(EditAd.this,
                    Mainpage.class);
            startActivity(intent);

            EditAd.this.finish();
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
        image.setVisibility(View.VISIBLE);
        textbookPicture.setVisibility(View.INVISIBLE);
        if (data != null && requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK){
            Uri selectedImage = data.getData();
            image.setImageURI(selectedImage);
            imageButtonSet = true;
        }

    }


    private void formatFile(){
        try{
            File filesDir = getApplicationContext().getFilesDir();
            file = new File(filesDir, "image" + ".png");

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
            byte[] bitmapdata = bos.toByteArray();


            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();

        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    class backListener implements View.OnClickListener {
        public void onClick(View v) {

            Intent intent = new Intent(EditAd.this,
                    MyAds.class);
            startActivity(intent);

            EditAd.this.finish();
        }

    }
    private void showImage(String url){
        int width  = Resources.getSystem().getDisplayMetrics().widthPixels;
        String clhttp="https://res.cloudinary.com/hyoenehbs/image/upload";

        int strLen=clhttp.length();

        String newUrl=clhttp+"/c_scale,w_"+ width +url.substring(strLen-1);
        Log.d("HAHA", "tryurl: " + newUrl);
        Picasso
                .with(EditAd.this)
                .load(newUrl)
                .into(textbookPicture);
    }
}

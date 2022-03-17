package com.csc301.students.BookBarter;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.allen.library.SuperTextView;

import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import okhttp3.MediaType;
import okhttp3.MultipartBody;

public class CreateAdPage extends AppCompatActivity {
    private String tokenS;
    private List<TextbookList> textbook=null;
    private static List<String> titles=new ArrayList<>();
    private ListView textListView;
    private TextbookList oneTextbook =null;
    private ArrayAdapter textAdapter;
    private SearchView textSearchView;
    private static String succeed;
    private Map<String,TextbookList> textbookMap;
    private SuperTextView title_STV,courseCod_STV,edition_STV,author_STV;
    private Button menuButton, postAdButton;
    private ImageButton image;
    private EditText  descriptionText;
    public EditText priceText;
    private String title, description,price;
    private Bitmap imageBitmap;
    private Token token;
    private LinearLayout formLayout;
    private File file;
    private static final int RESULT_LOAD_IMAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ad_page);


        textbookMap=new HashMap<String,TextbookList>();
        //token = (Token) getApplication();
        //setup data
        iniP();
        setupToken();
        //setupList
        getData();
        //
        Toolbar toolbar = (Toolbar) findViewById(R.id.Mypost_toolbar);
        //create toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //create a back icon on toolbar
        if (getSupportActionBar() != null) {
            // Enable the Up button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //add menu and searchable configuration
        //EditText TextBookFilter= (EditText) findViewById(R.id.searchFilter);
        //listener for back on toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//返回
            }
        });


    }





    private void iniP(){
        textListView=(ListView) findViewById(R.id.textbookList);
        textSearchView=(SearchView) findViewById(R.id.SearchText);
        descriptionText = findViewById(R.id.description);
        priceText=findViewById(R.id.priceEd);
        textAdapter=new ArrayAdapter(CreateAdPage.this,android.R.layout.simple_list_item_1,titles);
        textListView.setAdapter(textAdapter);
        textListView.setTextFilterEnabled(true);
        textSearchView.setOnQueryTextListener(new TextsSearchView());
        title_STV=findViewById(R.id.textTitle);
        author_STV=findViewById(R.id.author);
        edition_STV=findViewById(R.id.edition);
        courseCod_STV=findViewById(R.id.courseCode);
        formLayout=findViewById(R.id.form);
        textListView.setOnItemClickListener(new ListItemListener());
        textListView.setVisibility(View.GONE);
        postAdButton = findViewById(R.id.postads);
        menuButton = findViewById(R.id.menu);
        postAdButton.setOnClickListener(new CreateAdPage.postListener());
        menuButton.setOnClickListener(new CreateAdPage.menuListener());


        image = findViewById(R.id.image);
        image.setOnClickListener(new CreateAdPage.imageListener());
        textSearchView.setVisibility(View.GONE);
        token = (Token) getApplication();
        title_STV.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener(){
            @Override
            public void onClickListener(SuperTextView superTextView) {
                textSearchView.setVisibility(View.VISIBLE);
                textListView.setVisibility(View.VISIBLE);
                hideForm();

            }
        });


    }
    private void hideForm(){
        formLayout.setVisibility(View.GONE);
        descriptionText.setVisibility(View.GONE);
        title_STV.setVisibility(View.GONE);
        postAdButton.setVisibility(View.GONE);
        image.setVisibility(View.GONE);
        //textListView.setVisibility(View.GONE);
        //textListView.setVisibility(View.GONE);
    }
    private  void showForm(){
        formLayout.setVisibility(View.VISIBLE);
        descriptionText.setVisibility(View.VISIBLE);
        title_STV.setVisibility(View.VISIBLE);
        postAdButton.setVisibility(View.VISIBLE);
        image.setVisibility(View.VISIBLE);
    }
    private  void setupToken(){
        SharedPreferences sp = getSharedPreferences("loginToken", 0);
        tokenS=sp.getString("token",null);
        token.setToken(tokenS);

    }
    class ListItemListener implements AdapterView.OnItemClickListener{
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String o = (String) textListView.getItemAtPosition(position);
            oneTextbook=textbookMap.get(o);
            title_STV.setLeftString("Title: " + oneTextbook.getTitle());
            courseCod_STV.setLeftString("Course Code: " +oneTextbook.getCourseCode());
            author_STV.setLeftString("Author: " + oneTextbook.getAuthor());
            edition_STV.setLeftString("Ed:" + oneTextbook.getEdition());
            title= oneTextbook.getTitle();
            textListView.setVisibility(View.GONE);
            textSearchView.setVisibility(View.GONE);
            showForm();
        }
    }
    class TextsSearchView implements SearchView.OnQueryTextListener {
        @Override
        public boolean onQueryTextSubmit(String query) {
            textListView.setVisibility(View.GONE);

            return false;
        }
        @Override
        public boolean onQueryTextChange(String newText) {
            textListView.setVisibility(View.VISIBLE);
            textAdapter.getFilter().filter(newText);
            return false;
        }

    }
    @Override
    protected void onResume() {
        super.onResume();

    }
    public void addData(){
        titles.clear();
        textbookMap.clear();
        for (final TextbookList text : textbook) {
            android.util.Log.d("strToIntTest", "textbook=" +text.getTitle());
            String key=text.getCourseCode()+":"+text.getTitle();
            textbookMap.put( key,text);
            titles.add(text.getCourseCode()+":"+text.getTitle());
        }
    }

    public void getData() {
        Call<JsonResult> call = RetrofitClient.getInstance().getApi().getTextbooks("Bearer " + tokenS);
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {

                JsonResult res = response.body();
                textbook =res.getTextbookList();
                succeed=res.getSuccess();
                //succeed="succeed";
                android.util.Log.d("strToIntTest", "success=" +succeed);
                android.util.Log.d("strToIntTest", "textbook=" +textbook.toString());

                addData();

            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                t.printStackTrace();


            }
        });
    }
    class postListener implements View.OnClickListener {
        public void onClick(View v) {

            token = (Token) getApplication();
            //title = titleText.getText().toString();
            description = descriptionText.getText().toString();
            imageBitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
            price=priceText.getText().toString();

            final Intent intent = new Intent(CreateAdPage.this,
                    Mainpage.class);
            if (price.length() == 0){
                Toast.makeText(getBaseContext(), "Please Enter Selling Price",
                        Toast.LENGTH_SHORT).show();
            }
            else if(oneTextbook==null){
                Toast.makeText(getBaseContext(), "Select Textbook First",
                        Toast.LENGTH_SHORT).show();
            }
            else if (title.length() == 0 && description.length() == 0){
                Toast.makeText(getBaseContext(), "Title and Description cannot be empty",
                        Toast.LENGTH_SHORT).show();

            } else if (title.length() == 0) {
                Toast.makeText(getBaseContext(), "Title cannot be empty",
                        Toast.LENGTH_SHORT).show();
            } else if (description.length() == 0) {
                Toast.makeText(getBaseContext(), "Description cannot be empty",
                        Toast.LENGTH_SHORT).show();
            } else if (imageBitmap == null) {
                Toast.makeText(getBaseContext(), "Please upload image",
                        Toast.LENGTH_SHORT).show();
            } else{
                formatFile();
                RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                MultipartBody.Builder builder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM);
                builder.addFormDataPart("upload", file.getName(), imageBody);
                builder.addFormDataPart("description", description);
                builder.addFormDataPart("title",oneTextbook.getTitle());
                builder.addFormDataPart("edition", oneTextbook.getEdition());
                builder.addFormDataPart("courseCode", oneTextbook.getCourseCode());
                builder.addFormDataPart("getAuthor", oneTextbook.getAuthor());
                builder.addFormDataPart("textId",oneTextbook.getId());
                builder.addFormDataPart("price", price);
                List<MultipartBody.Part> body = builder.build().parts();

                Call<JsonResult> call = RetrofitClient.getInstance()
                        .getApi()
                        .createPost("Bearer " + token.getToken(),body);

                call.enqueue(new Callback<JsonResult>() {
                    @Override
                    public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                        JsonResult res = response.body();
                        if(res.getSuccess().equals("true")){
                            startActivity(intent);
                            Toast.makeText(CreateAdPage.this,"Post successful ", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(CreateAdPage.this, res.getSuccess(), Toast.LENGTH_LONG).show();
                        }


                    }

                    @Override
                    public void onFailure(Call<JsonResult> call, Throwable t) {
                        Toast.makeText(CreateAdPage.this, "fail to connect", Toast.LENGTH_LONG).show();
                        Toast.makeText(CreateAdPage.this, t.getMessage(), Toast.LENGTH_LONG).show();


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
    class menuListener implements View.OnClickListener {
        public void onClick(View v) {

            Intent intent = new Intent(CreateAdPage.this,
                    Mainpage.class);
            startActivity(intent);

            CreateAdPage.this.finish();
        }

    }
}







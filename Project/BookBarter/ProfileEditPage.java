package com.csc301.students.BookBarter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//Dazhi Chen: Allow user to edit the profile
public class ProfileEditPage extends AppCompatActivity {
    private Button done;
    private EditText text;
    private String flag;
    private String original_text;
    private Token token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit_page);


        Toolbar toolbar = (Toolbar) findViewById(R.id.userprofile_toolbar);
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

        done = (Button) findViewById(R.id.edit_done);
        done.setOnClickListener(new BtnClickListener());

        text = (EditText) findViewById(R.id.edittext_userprofile);

        //change title
        Intent getIntent = getIntent();
        flag = getIntent.getStringExtra("Flag");
        original_text = find_original_text(flag);
        TextView toolbar_title = (TextView) findViewById(R.id.userprofile_toolbar_title);
        ;
        toolbar_title.setText("Set " + flag);

        //get token
        token = (Token) getApplication();

    }

    class BtnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String text_edited = text.getText().toString();
            if (text_edited.isEmpty() || text_edited.contains(" ")) {
                Toast.makeText(ProfileEditPage.this, "Invalid Input", Toast.LENGTH_SHORT).show();
            } else if (text_edited.equals(original_text)) {
                Toast.makeText(ProfileEditPage.this, "Input is the Same", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                sendData(text_edited);
                finish();
            }
        }
    }

    private String find_original_text(String flag) {
        String result = null;
        if (flag.equals("password")) {
            return result;
        }
        if (flag.equals("campus")) {
            result = getIntent().getStringExtra("campus");
        }
        if (flag.equals("yearOfStudy")) {
            result = getIntent().getStringExtra("yearOfStudy");
        }
        if (flag.equals("name")) {
            result = getIntent().getStringExtra("name");
        }

        return result;
    }

    public void sendData(String data) {
        JsonObject body = new JsonObject();
        body.addProperty(flag, data);
        Call<JsonResult> call = RetrofitClient.getInstance()
                .getApi()
                .editUserInfo("Bearer " + token.getToken(), body);
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {

                JsonResult res = response.body();

                if (Integer.toString(response.code()).equals("200")) {
                    Toast.makeText(ProfileEditPage.this,
                            "Success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileEditPage.this,
                            "Failed, Reson Code " + response.code(), Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

}

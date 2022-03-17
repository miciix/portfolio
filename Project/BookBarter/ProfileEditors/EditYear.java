package com.csc301.students.BookBarter.ProfileEditors;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.csc301.students.BookBarter.JsonResult;
import com.csc301.students.BookBarter.R;
import com.csc301.students.BookBarter.RetrofitClient;
import com.csc301.students.BookBarter.Token;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
//Dazhi Chen: allow user to edit year of study and send to database
public class EditYear extends AppCompatActivity {

    private Button done;
    private RadioGroup radioGroup;
    private String flag;
    private String original_text;
    private Token token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_year);

        //every editor has a done button
        done = (Button) findViewById(R.id.edit_done);
        done.setOnClickListener(new BtnClickListener());

        radioGroup = findViewById(R.id.radio_group);
        radioGroup.check(R.id.rb_no);

        //get token
        token = (Token) getApplication();

    }

    class BtnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String radio_text;
            String text_edited;
            int id = radioGroup.getCheckedRadioButtonId();
            RadioButton rb = findViewById(id);
            radio_text = (String) rb.getText();
            if(radio_text.equals("Prefer Not To Say")){
                text_edited = "";
            }else{
                text_edited = radio_text;
            }
            sendData(text_edited);
            finish();

        }
    }


    public void sendData(String data) {
        JsonObject body = new JsonObject();
        body.addProperty("yearOfStudy", data);
        Call<JsonResult> call = RetrofitClient.getInstance()
                .getApi()
                .editUserInfo("Bearer " + token.getToken(), body);
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {

                JsonResult res = response.body();

                if (Integer.toString(response.code()).equals("200")) {
                    Toast.makeText(getApplicationContext(),
                            "Success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Failed:" + response.code(), Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }



}

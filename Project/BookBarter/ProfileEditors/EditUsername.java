package com.csc301.students.BookBarter.ProfileEditors;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.csc301.students.BookBarter.JsonResult;
import com.csc301.students.BookBarter.R;
import com.csc301.students.BookBarter.RetrofitClient;
import com.csc301.students.BookBarter.Token;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
//Dazhi Chen: allow user to edit username and send to database
public class EditUsername extends AppCompatActivity {

    private Button done;
    private EditText text;
    private String flag;
    private String original_text;
    private Token token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_username);

        //every editor has a done button
        done = (Button) findViewById(R.id.edit_done);
        done.setOnClickListener(new BtnClickListener());

        text = (EditText) findViewById(R.id.edit_username);

        //get token
        token = (Token) getApplication();

    }

    class BtnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String text_edited = text.getText().toString();
            if (text_edited.isEmpty() || text_edited.contains(" ")) {
                Toast.makeText(getApplicationContext(),
                        "Invalid Input", Toast.LENGTH_SHORT).show();
            } else {
                sendData(text_edited);
                finish();
            }
        }
    }


    public void sendData(String data) {
        JsonObject body = new JsonObject();
        body.addProperty("name", data);
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

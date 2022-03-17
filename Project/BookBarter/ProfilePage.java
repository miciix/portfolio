package com.csc301.students.BookBarter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.allen.library.SuperTextView;
import com.csc301.students.BookBarter.ProfileEditors.EditCampus;
import com.csc301.students.BookBarter.ProfileEditors.EditPassword;
import com.csc301.students.BookBarter.ProfileEditors.EditUsername;
import com.csc301.students.BookBarter.ProfileEditors.EditYear;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//Dazhi Chen: Retrive data from database and show it on the Profile Page
public class ProfilePage extends AppCompatActivity {
    private SuperTextView yearofstudy_STV;
    private SuperTextView username_STV;
    private SuperTextView campus_STV;
    private SuperTextView password_STV;
    private Intent intent;
    private Token token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);


        Toolbar toolbar = (Toolbar) findViewById(R.id.userprofile_toolbar);
        //create toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //create a back icon on toolbar
        if (getSupportActionBar() != null) {
            // Enable the Up button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        password_STV = (SuperTextView) findViewById(R.id.password);
        campus_STV = (SuperTextView) findViewById(R.id.campus);
        yearofstudy_STV = (SuperTextView) findViewById(R.id.year_of_study);
        username_STV = (SuperTextView) findViewById(R.id.user_name);

        //Create a listener
        ButtonListener btnLisener = new ButtonListener();

        password_STV.setOnSuperTextViewClickListener(btnLisener);
        yearofstudy_STV.setOnSuperTextViewClickListener(btnLisener);
        username_STV.setOnSuperTextViewClickListener(btnLisener);
        campus_STV.setOnSuperTextViewClickListener(btnLisener);

        token = (Token) getApplication();

        //listener for back on toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//返回
            }
        });

        //get all data
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    public void getData() {
        Call<JsonResult> call = RetrofitClient.getInstance().getApi().getUserInfo("Bearer " + token.getToken());
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


            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    class ButtonListener implements SuperTextView.OnSuperTextViewClickListener {
        @Override
        public void onClickListener(SuperTextView superTextView) {
            switch (superTextView.getId()) {
                case R.id.password:
                    intent = new Intent(ProfilePage.this,
                            EditPassword.class);
                    intent.putExtra("Flag", "password");
                    startActivity(intent);
                    break;
                case R.id.campus:
                    intent = new Intent(ProfilePage.this,
                            EditCampus.class);
                    intent.putExtra("Flag", "campus");
                    intent.putExtra("campus",campus_STV.getRightString());
                    startActivity(intent);
                    break;
                case R.id.year_of_study:
                    intent = new Intent(ProfilePage.this,
                            EditYear.class);
                    intent.putExtra("Flag", "yearOfStudy");
                    intent.putExtra("yearOfStudy",yearofstudy_STV.getRightString());
                    startActivity(intent);
                    break;
                case R.id.user_name:
                    intent = new Intent(ProfilePage.this,
                            EditUsername.class);
                    intent.putExtra("Flag", "name");
                    intent.putExtra("name",username_STV.getRightString());
                    startActivity(intent);
                    break;
            }
        }

    }

}

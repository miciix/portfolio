package com.csc301.students.BookBarter;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class BookDBPage extends AppCompatActivity {

    private ArrayList<String> mBookNames = new ArrayList<String>();
    private ArrayList<String> mCourseCode = new ArrayList<String>();
    private ArrayList<String> mEdition  = new ArrayList<String>();
    private ArrayList<String> mAuthor = new ArrayList<String>();
    private ArrayList<String> mId = new ArrayList<String>();
    private Token token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_dbpage);
        token = (Token) getApplication();
        getBookInfo();
        //getNotification();
    }

    public void getBookInfo(){
        Call<JsonResult> call = RetrofitClient.getInstance().getApi()
                .getTextbook("Bearer " + token.getToken());

        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult result = response.body();
                List<TextbookList> tbLst = result.getTextbookList();
                for(TextbookList books : tbLst){
                    mBookNames.add(books.getTitle());
                    mCourseCode.add(books.getCourseCode());
                    mEdition.add(books.getEdition());
                    mAuthor.add(books.getAuthor());
                    mId.add(books.getId());
                    //concel all notice
                    //cancelNotice(books.getId());
                }
                initRecyclerView();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mBookNames,
                mCourseCode,mEdition,mAuthor,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnButtonClickListeners(new RecyclerViewAdapter.OnButtonClickListeners() {
            @Override
            public void onClick(int position) {
                //android.util.Log.d("strToIntTest", "idP=" + position);
                //android.util.Log.d("strToIntTest", "id=" + mId.get(position));
                setNotice(mId.get(position));
            }
        });
    }
    private void setNotice(String bookId){
        FirebaseMessaging.getInstance().subscribeToTopic(bookId)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        /*
                        String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();*/

                        if (!task.isSuccessful()) {
                            android.util.Log.d("strToIntTest", "subscribe=" +"fail");
                            Toast.makeText(BookDBPage.this,"add to notice list unsuccessful", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(BookDBPage.this,"add to notice list", Toast.LENGTH_LONG).show();
                            android.util.Log.d("strToIntTest", "subscribe=" +"success");
                        }

                    }
                });

    }
    private void cancelNotice(String bookId){
        FirebaseMessaging.getInstance().unsubscribeFromTopic(bookId)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        /*
                        String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();*/

                        if (!task.isSuccessful()) {
                            android.util.Log.d("strToIntTest", "subscribe=" +"fail");
                            Toast.makeText(BookDBPage.this,"add to notice list unsuccessful", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(BookDBPage.this,"add to notice list", Toast.LENGTH_LONG).show();
                            android.util.Log.d("strToIntTest", "subscribe=" +"success");
                        }

                    }
                });

    }

}

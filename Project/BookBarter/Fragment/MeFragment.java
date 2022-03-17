package com.csc301.students.BookBarter.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.allen.library.SuperTextView;
import com.csc301.students.BookBarter.JsonResult;
import com.csc301.students.BookBarter.MainActivity;
import com.csc301.students.BookBarter.MyAds;
import com.csc301.students.BookBarter.ProfilePage;
import com.csc301.students.BookBarter.R;
import com.csc301.students.BookBarter.RetrofitClient;
import com.csc301.students.BookBarter.Token;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//Dazhi Chen: profile page display
public class MeFragment extends Fragment {
    private String content;
    private SuperTextView editProfileSTV, emailSTV, logoutSTV, myAdsSTV;
    private Intent intent;
    String string;
    private Token token;

    public static MeFragment newInstance(String content) {
        return new MeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_me, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        editProfileSTV = (SuperTextView) getActivity().findViewById(R.id.account_setting);
        emailSTV = getActivity().findViewById(R.id.emailSTV);
        logoutSTV = getActivity().findViewById(R.id.logoutSTV);
        myAdsSTV = getActivity().findViewById(R.id.myadsSTV);

        //Create a listener
        ButtonListener btnLisener = new ButtonListener();

        //Listen all the buttons
        editProfileSTV.setOnSuperTextViewClickListener(btnLisener);
        logoutSTV.setOnSuperTextViewClickListener(btnLisener);
        myAdsSTV.setOnSuperTextViewClickListener(btnLisener);

        token = (Token) getActivity().getApplication();
        getEmail();

    }

    public void getEmail(){
        Call<JsonResult> call = RetrofitClient.getInstance().getApi().getUserInfo("Bearer " + token.getToken());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {

                JsonResult res = response.body();
                if (res.getEmail() != null) {
                    emailSTV.setRightString(res.getEmail());
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
                case R.id.account_setting:
                    intent = new Intent(getActivity(),
                            ProfilePage.class);
                    startActivityForResult(intent, 1);
                    break;
                case R.id.logoutSTV:
                    SharedPreferences sp = getActivity().
                            getSharedPreferences("loginToken", 0);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.clear();
                    editor.commit();
                    intent = new Intent(getActivity(),
                            MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                    break;
                case R.id.myadsSTV:
                    intent = new Intent(getActivity(), MyAds.class);
                    startActivity(intent);
                    break;
            }
        }

    }
}
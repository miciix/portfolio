package com.csc301.students.BookBarter.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.csc301.students.BookBarter.AdsViewPage;
import com.csc301.students.BookBarter.JsonResult;
import com.csc301.students.BookBarter.R;
import com.csc301.students.BookBarter.RetrofitClient;
import com.csc301.students.BookBarter.SearchAds.Data;
import com.csc301.students.BookBarter.SearchAds.MyAdapter;
import com.csc301.students.BookBarter.Token;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//Dazhi Chen: Favourite page for user's favourite post
public class FavouriteFragment extends Fragment {

    private ListView Search_List;
    private SearchView searchView;
    private ArrayAdapter<String> SearchAdapter;
    private ListView Data_List;
    private MyAdapter DataAdapter = null;
    private LinkedList<Data> mData = null;
    private TextView txt_empty;
    private Token token;
    private Intent intent;
    private List<String> data = new ArrayList<String>();
    private JsonArray interested_lst;
    private boolean isGetData = false;

    public static FavouriteFragment newInstance(String content) {
        return new FavouriteFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_interested, container, false);
    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //get token
        token = (Token) getActivity().getApplication();

        //show data
        Data_List = (ListView) getActivity().findViewById(R.id.interested_post_list);
        mData = new LinkedList<Data>();
        DataAdapter = new MyAdapter(mData, getActivity());
        Data_List.setAdapter(DataAdapter);
        ListListener listListener = new ListListener();
        Data_List.setOnItemClickListener(listListener);

        //if no data shown
        txt_empty = (TextView) getActivity().findViewById(R.id.interested_txt_empty);
        txt_empty.setText("No Data ~");
        Data_List.setEmptyView(txt_empty);

    }


    class ListListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Data clicked_post;
            clicked_post = DataAdapter.get(position);
            intent = new Intent(getActivity(),
                    AdsViewPage.class);
            intent.putExtra("data", clicked_post);

            startActivity(intent);
        }
    }
    //get all post from database
    public void getData() {


        Call<JsonResult> call = RetrofitClient.getInstance().getApi().getAllposts("Bearer " + token.getToken());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                String email;
                String description;
                String title;
                String id;
                String price;
                String img;

                JsonResult res = response.body();
                JsonArray interestedArray = res.getAllPosts();
                data.clear();
                DataAdapter.clear();
                for (int i = 0; i < interestedArray.size(); i++) {

                    JsonObject post = (JsonObject) interestedArray.get(i);

                    id = post.get("_id").getAsString();
                    if (!interested_lst.contains(new JsonPrimitive(id))){
                        continue;
                    }
                    if (post.get("email") == null) {
                        email = "no email";
                    } else {
                        email = post.get("email").getAsString();
                    }

                    if (post.get("description") == null) {
                        description = "no description";
                    } else {
                        description = post.get("description").getAsString();
                    }
                    if (post.get("title") == null) {
                        title = "no title";
                    } else {
                        title = post.get("title").getAsString();
                    }
                    if (post.get("price") == null) {
                        price = "No Price";
                    } else {
                        price = "$" + post.get("price").getAsString();
                    }

                    img = post.get("image").getAsString();

                    DataAdapter.add(new Data(img, email, description, title, id, price));
                    data.add(title);
                }
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
    public void getInterested() {
        Log.d("HAHA", "getting data");
        Call<JsonResult> call = RetrofitClient.getInstance().getApi().getInterested("Bearer " + token.getToken());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {

                JsonResult res = response.body();
                if (res.getInterested() != null) {
                    interested_lst = res.getInterested();
                }
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        // entering Fragment
        Log.d("HAHA", "Animation");
        if (enter && !isGetData) {
            isGetData = true;
            //get data
            getInterested();
            getData();
        } else {
            isGetData = false;
        }
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public void onResume() {
        Log.d("HAHA", "onResume");
        super.onResume();
        if (!isGetData) {
            //get data
            getInterested();
            getData();
            isGetData = true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isGetData = false;
    }

}
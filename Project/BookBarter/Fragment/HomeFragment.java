package com.csc301.students.BookBarter.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.csc301.students.BookBarter.AdsViewPage;
import com.csc301.students.BookBarter.JsonResult;
import com.csc301.students.BookBarter.R;
import com.csc301.students.BookBarter.RetrofitClient;
import com.csc301.students.BookBarter.SearchAds.Data;
import com.csc301.students.BookBarter.SearchAds.MyAdapter;
import com.csc301.students.BookBarter.Token;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//Dazhi Chen: Home page display
public class HomeFragment extends Fragment implements AdapterView.OnItemClickListener {
    // Declare Variables
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


    public static HomeFragment newInstance(String content) {
        return new HomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Search_List = (ListView) getActivity().findViewById(R.id.list_view);
        searchView = (SearchView) getActivity().findViewById(R.id.search_view);
        //get token
        token = (Token) getActivity().getApplication();
        //get data
        getData();

        SearchAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, data);
        Search_List.setAdapter(SearchAdapter);
        //listview start filtering
        Search_List.setTextFilterEnabled(false);
        //do not show list onstart
        Search_List.setVisibility(View.GONE);
        //match_parent
        searchView.setIconifiedByDefault(false);
        //show search button
        searchView.setSubmitButtonEnabled(true);
        //give default string
        searchView.setQueryHint("Search");


        SearchListener searchListener = new SearchListener();
        searchView.setOnQueryTextListener(searchListener);

        Search_List.setOnItemClickListener(this);

        //show data
        Data_List = (ListView) getActivity().findViewById(R.id.list_view_data);
        mData = new LinkedList<Data>();
        DataAdapter = new MyAdapter(mData, getActivity());
        Data_List.setAdapter(DataAdapter);
        ListListener listListener = new ListListener();
        Data_List.setOnItemClickListener(listListener);

        //if no data shown
        txt_empty = (TextView) getActivity().findViewById(R.id.txt_empty);
        txt_empty.setText("No Data ~");
        Data_List.setEmptyView(txt_empty);

    }

    //listener for search bar list
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Data searched_post;
        //get user clicked item
        String title = parent.getItemAtPosition(position).toString();
        try {
            searched_post = DataAdapter.get(data.indexOf(title));
        } catch (Exception e) {
            return;
        }
        DataAdapter.clear();
        DataAdapter.add(searched_post);
        Search_List.setVisibility(View.GONE);
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

    //listener for search bar
    class SearchListener implements SearchView.OnQueryTextListener {
        //listen search button clicked
        @Override
        public boolean onQueryTextSubmit(String query) {
            LinkedList<Data> temp = (LinkedList<Data>) mData.clone();
            DataAdapter.clear();
            for (Data post : temp) {
                if (post.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    DataAdapter.add(post);
                }

            }
            Search_List.setVisibility(View.GONE);
            return false;

        }

        //listen typing
        @Override
        public boolean onQueryTextChange(String newText) {
            if (TextUtils.isEmpty(newText)) {
                Search_List.setVisibility(View.GONE);
                SearchAdapter.getFilter().filter(null);
                getData();
            } else {
                SearchAdapter.getFilter().filter(newText);
                Search_List.setVisibility(View.VISIBLE);
            }
            return false;

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
                JsonArray PostArray = res.getAllPosts();
                data.clear();
                DataAdapter.clear();
                for (int i = 0; i < PostArray.size(); i++) {

                    JsonObject post = (JsonObject) PostArray.get(i);

                    id = post.get("_id").getAsString();
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


}



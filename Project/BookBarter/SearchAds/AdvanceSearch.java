package com.csc301.students.BookBarter.SearchAds;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.csc301.students.BookBarter.R;

// Dazhi Chen: Search function for the App
public class AdvanceSearch extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner spin_coursecode;
    private Spinner spin_author;
    private Spinner spin_edition;
    private Button done;
    private String coursecode_choose;
    private String author_choose;
    private String edition_choose;
 
    private boolean one_selected = false;
    private boolean two_selected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.advance_search_toolbar);
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
        done = (Button) findViewById(R.id.search_done);
        done.setOnClickListener(new BtnClickListener());
        initView();
    }
    class BtnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.d("haha", "c:"+coursecode_choose+"a:"+author_choose+"e:"+edition_choose);
        }
    }

    private void initView() {
        spin_coursecode = (Spinner) findViewById(R.id.search_course_code);
        spin_author = (Spinner) findViewById(R.id.search_author);
        spin_edition = (Spinner) findViewById(R.id.search_edition);
        String[] coursecode = new String[]{
                "1", "2", "3", "4", "5", "6", "7"
        };

        String[] author = new String[]{
                "1", "2", "3", "4", "5", "6", "7"
        };

        String[] edition = new String[]{
                "1", "2", "3", "4", "5", "6", "7"
        };
        ArrayAdapter<String> coursecode_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, coursecode);
        coursecode_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_coursecode.setAdapter(coursecode_adapter);

        ArrayAdapter<String> author_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, author);
        coursecode_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_author.setAdapter(author_adapter);

        ArrayAdapter<String> edition_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, edition);
        edition_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_edition.setAdapter(author_adapter);

        spin_coursecode.setOnItemSelectedListener(this);
        spin_author.setOnItemSelectedListener(this);
        spin_edition.setOnItemSelectedListener(this);

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.search_course_code:

                Toast.makeText(this, "course code：" + parent.getItemAtPosition(position).toString(),
                        Toast.LENGTH_SHORT).show();
                coursecode_choose = parent.getItemAtPosition(position).toString();
                break;
            case R.id.search_author:

                Toast.makeText(this, "author：" + parent.getItemAtPosition(position).toString(),
                        Toast.LENGTH_SHORT).show();
                author_choose = parent.getItemAtPosition(position).toString();
                break;
            case R.id.search_edition:

                Toast.makeText(this, "edition：" + parent.getItemAtPosition(position).toString(),
                        Toast.LENGTH_SHORT).show();
                edition_choose = parent.getItemAtPosition(position).toString();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}

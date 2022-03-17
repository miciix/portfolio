package com.csc301.students.BookBarter;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;

import android.os.Build;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.csc301.students.BookBarter.Fragment.FavouriteFragment;
import com.csc301.students.BookBarter.Fragment.HomeFragment;
import com.csc301.students.BookBarter.Fragment.MeFragment;
import com.csc301.students.BookBarter.SearchAds.AdvanceSearch;
import com.google.firebase.FirebaseApp;

//Dazhi Chen: Main Page for Android APP and its Functionalities.
public class Mainpage extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    //Fragment Object
    private MeFragment mf;
    private HomeFragment hf;
    private FavouriteFragment ff;
    private RadioButton fav,home,user;
    private FragmentManager fManager;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);
        fManager = getSupportFragmentManager();

        //show the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //hide actionbar title
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //setup Notification
        setupNotification();
        //set listener to BottomNavigation bar
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(this);

        fav = findViewById(R.id.rb_like);
        home = findViewById(R.id.rb_home);
        user = findViewById(R.id.rb_me);

        //initialize first fragment
        TextView toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        toolbar_title.setText(getString(R.string.item_home));
        FragmentTransaction transaction = fManager.beginTransaction();
        hf = HomeFragment.newInstance(getResources().getString(R.string.item_home));
        transaction.add(R.id.sub_content, hf).commit();
        FirebaseApp.initializeApp(this);

    }
    private void setupNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = "book subscribe channel";
            String channelName = "Notification";
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

    }
    //change the fragment base on user choose
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        TextView toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        FragmentTransaction transaction = fManager.beginTransaction();
        hideAllFragment(transaction);
        switch (checkedId) {
            case R.id.rb_home:
                if (hf == null) {
                    toolbar_title.setText(getString(R.string.item_home));
                    hf = HomeFragment.newInstance(getString(R.string.item_home));
                    transaction.add(R.id.sub_content, hf);
                } else {
                    transaction.show(hf);
                    toolbar_title.setText(getString(R.string.item_home));

                }
                fav.setTextColor(ContextCompat.getColor(getApplicationContext(),
                        R.color.textGrey));
                home.setTextColor(ContextCompat.getColor(getApplicationContext(),
                        R.color.colorPrimary));
                user.setTextColor(ContextCompat.getColor(getApplicationContext(),
                        R.color.textGrey));
                break;
            case R.id.rb_like:
                if (ff == null) {
                    toolbar_title.setText(getString(R.string.item_favourite));
                    ff = FavouriteFragment.newInstance(getString(R.string.item_favourite));
                    transaction.add(R.id.sub_content, ff);
                } else {
                    transaction.show(ff);
                    toolbar_title.setText(getString(R.string.item_favourite));
                }

                fav.setTextColor(ContextCompat.getColor(getApplicationContext(),
                        R.color.colorPrimary));
                home.setTextColor(ContextCompat.getColor(getApplicationContext(),
                        R.color.textGrey));
                user.setTextColor(ContextCompat.getColor(getApplicationContext(),
                        R.color.textGrey));
                break;
            case R.id.rb_me:
                if (mf == null) {
                    toolbar_title.setText(getString(R.string.item_person));
                    mf = MeFragment.newInstance(getString(R.string.item_person));
                    transaction.add(R.id.sub_content, mf);
                } else {
                    transaction.show(mf);
                    toolbar_title.setText(getString(R.string.item_person));
                }
                fav.setTextColor(ContextCompat.getColor(getApplicationContext(),
                        R.color.textGrey));
                home.setTextColor(ContextCompat.getColor(getApplicationContext(),
                        R.color.textGrey));
                user.setTextColor(ContextCompat.getColor(getApplicationContext(),
                        R.color.colorPrimary));
                break;

        }


        transaction.commitAllowingStateLoss();
    }

    //hide all fragment
    private void hideAllFragment(FragmentTransaction fragmentTransaction) {
        if (hf != null) fragmentTransaction.hide(hf);
        if (ff != null) fragmentTransaction.hide(ff);
        if (mf != null) fragmentTransaction.hide(mf);
    }

    //create top right corner menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    //options in top right corner menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.postad:
                intent = new Intent(Mainpage.this,
                        CreateAdPage.class);
                startActivity(intent);
                break;
            case R.id.view_books:
                intent = new Intent(Mainpage.this,
                        BookDBPage.class);
                startActivity(intent);
                break;
            default:
        }
        return true;
    }
}

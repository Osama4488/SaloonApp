package com.example.saloonapp.Activities.User;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatTextView;
import android.view.MotionEvent;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.saloonapp.Adapters.User.SearchAutoCompleteAdapter;
import com.example.saloonapp.Fragments.User.UserAppointmentFragment;
import com.example.saloonapp.Fragments.User.UserHistoryFragment;
import com.example.saloonapp.Fragments.User.UserHomeFragment;
import com.example.saloonapp.Fragments.User.UserProfileFragment;
import com.example.saloonapp.Models.ParlourModel;
import com.example.saloonapp.R;

import java.util.ArrayList;
import java.util.List;

public class UserDrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnTouchListener {

    //Fragment
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private Fragment fragment;

    private NavigationView navigationView;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private MenuItem previousItem;
    private AppCompatTextView toolbarTitleTV;
    private AppCompatAutoCompleteTextView toolbarSearchTV;
    private View navigationHeader;
    private MenuItem searchIcon, cartIcon;

    private List<ParlourModel> allModelList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_drawer);

        bindControls();
        bindListeners();
        toolbarSetting();
        setUpSearhBarList();
        setUpHomePage();
    }

    private void setUpSearhBarList() {
        allModelList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            allModelList.add(new ParlourModel(
                    i,
                    "All " + i,
                    (double) i,
                    String.valueOf(i)
            ));
        }
        SearchAutoCompleteAdapter adapter = new SearchAutoCompleteAdapter(UserDrawerActivity.this, R.layout.item_search_bar, allModelList);
        toolbarSearchTV.setAdapter(adapter);
        toolbarSearchTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(UserDrawerActivity.this, allModelList.get(position).getParlourName()+"", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpHomePage() {
        navigationView.setCheckedItem(R.id.nav_home);
        setUpFragment(new UserHomeFragment(), "Home");
    }

    private void setUpFragment(Fragment frag, String toolbarTitle) {
        toolbarTitleTV.setText(toolbarTitle);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragment = frag;
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    private void bindListeners() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        toolbarSearchTV.setOnTouchListener(this);
    }

    private void bindControls() {
        toolbar = findViewById(R.id.toolbar);
        toolbarTitleTV = toolbar.findViewById(R.id.toolbarTitleTV);
        toolbarSearchTV = toolbar.findViewById(R.id.toolbarSearchTV);

        drawerLayout = findViewById(R.id.user_drawerDL);
        navigationView = findViewById(R.id.user_drawerNV);

        navigationHeader = navigationView.getHeaderView(0);
    }

    private void toolbarSetting() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public void onBackPressed() {
        if (toolbarSearchTV.getVisibility() == View.VISIBLE) {
            hideAndShowIcons(true);
            toolbarSearchTV.setVisibility(View.GONE);
            toolbarTitleTV.setVisibility(View.VISIBLE);
        } else if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
//            finishAffinity();
            super.onBackPressed();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_toolbar, menu);
        searchIcon = menu.findItem(R.id.action_search);
        cartIcon = menu.findItem(R.id.action_cart);
        return true;
    }

    private void hideAndShowIcons(Boolean value){
        searchIcon.setVisible(value);
        cartIcon.setVisible(value);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v == toolbarSearchTV){
            if(event.getAction() == MotionEvent.ACTION_UP) {
                if(event.getRawX() >= (toolbarSearchTV.getRight() - toolbarSearchTV.getCompoundDrawables()[2].getBounds().width())) {
                    startActivity(new Intent(getApplicationContext(), FiltersActivity.class));
                    hideAndShowIcons(true);
                    toolbarSearchTV.setVisibility(View.GONE);
                    toolbarTitleTV.setVisibility(View.VISIBLE);
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            toolbarSearchTV.setVisibility(View.GONE);
            toolbarTitleTV.setVisibility(View.VISIBLE);
            hideAndShowIcons(true);
        } else if (id == R.id.action_search){
            hideAndShowIcons(false);
            toolbarSearchTV.setVisibility(View.VISIBLE);
            toolbarTitleTV.setVisibility(View.GONE);
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        if (previousItem == item){
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        previousItem = item;

        int id = item.getItemId();

        String toolbarTitle = "";
        Boolean value = true;

        if (id == R.id.nav_home) {
            fragment = new UserHomeFragment();
            toolbarTitle = "Home";
            value = true;
        } else if (id == R.id.nav_appointment) {
            fragment = new UserAppointmentFragment();
            toolbarTitle = "Appointment";
            value = false;
        } else if (id == R.id.nav_profile) {
            fragment = new UserProfileFragment();
            toolbarTitle = "Profile";
            value = false;
        } else if (id == R.id.nav_history) {
            fragment = new UserHistoryFragment();
            toolbarTitle = "History";
            value = false;
        } else if (id == R.id.nav_logout) {

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        setUpFragment(fragment, toolbarTitle);
        hideAndShowIcons(value);
        return true;
    }
}
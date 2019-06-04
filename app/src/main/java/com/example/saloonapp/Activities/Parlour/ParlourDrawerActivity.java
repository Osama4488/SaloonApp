package com.example.saloonapp.Activities.Parlour;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.example.saloonapp.Fragments.Parlour.BookingsFragment;
import com.example.saloonapp.Fragments.Parlour.ExpertsFragment;
import com.example.saloonapp.Fragments.Parlour.ParlourProfileFragment;
import com.example.saloonapp.Fragments.Parlour.ServicesFragment;
import com.example.saloonapp.R;

public class ParlourDrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private Fragment fragment;

    private NavigationView navigationView;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private MenuItem previousItem;
    private AppCompatTextView toolbarTitleTV;
    private View navigationHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parlour_drawer);

        bindControls();
        bindListeners();
        toolbarSetting();
        setUpHomePage();

    }

    private void setUpHomePage() {
        navigationView.setCheckedItem(R.id.nav_services);
        setUpFragment(new ServicesFragment(), "Services");
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
    }

    private void bindControls() {
        toolbar = findViewById(R.id.toolbar);
        toolbarTitleTV = toolbar.findViewById(R.id.toolbarTitleTV);

        drawerLayout = findViewById(R.id.parlour_drawerDL);
        navigationView = findViewById(R.id.parlour_drawerNV);

        navigationHeader = navigationView.getHeaderView(0);
    }

    private void toolbarSetting() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
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
        getMenuInflater().inflate(R.menu.parlour_toolbar, menu);
        return true;
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


        if (id == R.id.nav_bookings) {
            fragment = new BookingsFragment();
            toolbarTitle = "Bookings";
        } else if (id == R.id.nav_experts) {
            fragment = new ExpertsFragment();
            toolbarTitle = "Experts";
        } else if (id == R.id.nav_profile) {
            fragment = new ParlourProfileFragment();
            toolbarTitle = "Profile";
        } else if (id == R.id.nav_services) {
            fragment = new ServicesFragment();
            toolbarTitle = "Services";
        } else if (id == R.id.nav_logout) {

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        setUpFragment(fragment, toolbarTitle);

        return true;
    }
}

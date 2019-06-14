package com.example.saloonapp.Activities.Parlour;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.Window;

import com.example.saloonapp.Activities.Common.LoginActivity;
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
    private AppCompatTextView toolbarTitleTV, navigationName, navigationEmail;
    private View navigationHeader;

    private String parlourRating, TAG = "PARLOUR_DRAWER_ACTIVITY";

    // Feedback dilaog Controls
    private AlertDialog suggestionDialog;
    private AppCompatButton dialog_suggestionGoBN;
    private AppCompatTextView dialog_suggestionQuestionTV;
    private AppCompatImageButton dialog_closeIB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parlour_drawer);

        bindControls();
        bindListeners();
        toolbarSetting();
        setUpHomePage();

        SharedPreferences sharedPreferences = getSharedPreferences("userDetails", MODE_PRIVATE);
        parlourRating = sharedPreferences.getString("rating", null);
        Double rating = Double.valueOf(parlourRating);
        if (rating <= 2.5) {
            showSuggestionDialog();
        }
    }


    private void showSuggestionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ParlourDrawerActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_suggestion, null);
        builder.setView(dialogView);

        bindDialogControls(dialogView);
        bindDialogListeners();
        setTextView();

        suggestionDialog = builder.create();
        suggestionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        suggestionDialog.setCanceledOnTouchOutside(true);
        suggestionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        suggestionDialog.show();
    }

    private void setTextView() {
        Double rating = Double.valueOf(parlourRating);
        if (rating == 2.5) {
            dialog_suggestionQuestionTV.setText("Your Parlour rating is " + parlourRating + " would you like to see the suggested Parlours near you?");
        } else if (rating < 2.5) {
            dialog_suggestionQuestionTV.setText("Your Parlour rating is below " + parlourRating + " would you like to see the suggested Parlours near you?");
        }
    }

    private void bindDialogListeners() {
        dialog_suggestionGoBN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ParlourDrawerActivity.this, SuggestionActivity.class));
                suggestionDialog.dismiss();
            }
        });

        dialog_closeIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                suggestionDialog.dismiss();
            }
        });
    }

    private void bindDialogControls(View dialogView) {
        dialog_suggestionGoBN = dialogView.findViewById(R.id.dialog_goBN);
        dialog_suggestionQuestionTV = dialogView.findViewById(R.id.dialog_questionTV);
        dialog_closeIB = dialogView.findViewById(R.id.dialog_closeIB);
    }

    private void setUpHomePage() {
        Boolean refreshFragment;
        try {
            refreshFragment = getIntent().getExtras().getBoolean("refresh", false);
        } catch (Exception e) {
            refreshFragment = false;
            Log.e(TAG, "setUpHomePage: " + e);
        }
        if (refreshFragment) {
            navigationView.setCheckedItem(R.id.nav_bookings);
            setUpFragment(new BookingsFragment(), "Bookings");
        } else {
            navigationView.setCheckedItem(R.id.nav_services);
            setUpFragment(new ServicesFragment(), "Services");
        }
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
        navigationName = navigationHeader.findViewById(R.id.header_name);
        navigationEmail = navigationHeader.findViewById(R.id.header_email);

        SharedPreferences sharedPreferences = getSharedPreferences("userDetails", MODE_PRIVATE);
        String name = sharedPreferences.getString("fullName", null);
        String email = sharedPreferences.getString("userName", null);
        if (name != null && email != null) {
            navigationName.setText(name);
            navigationEmail.setText(email);
        }
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
            finishAffinity();
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
            logout();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        setUpFragment(fragment, toolbarTitle);

        return true;
    }

    private void logout() {
        SharedPreferences.Editor editor = getSharedPreferences("userDetails", MODE_PRIVATE).edit();
        editor.clear().apply();
        startActivity(new Intent(ParlourDrawerActivity.this, LoginActivity.class));
    }
}

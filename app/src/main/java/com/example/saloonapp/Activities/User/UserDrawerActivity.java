package com.example.saloonapp.Activities.User;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.saloonapp.Activities.Common.LoginActivity;
import com.example.saloonapp.Adapters.User.SearchAutoCompleteAdapter;
import com.example.saloonapp.Fragments.User.UserAppointmentFragment;
import com.example.saloonapp.Fragments.User.UserHomeFragment;
import com.example.saloonapp.Fragments.User.UserProfileFragment;
import com.example.saloonapp.Models.ParlourModel;
import com.example.saloonapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserDrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnTouchListener {

    //Fragment
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private Fragment fragment;

    private NavigationView navigationView;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private MenuItem previousItem;
    private AppCompatTextView toolbarTitleTV, navigationName, navigationEmail;
    private AppCompatAutoCompleteTextView toolbarSearchTV;
    private View navigationHeader;
    private MenuItem searchIcon, cartIcon;
    private String userEmail, parlourName, parlourId, bookingId;

    private List<ParlourModel> allModelList;

    //Api Strings
    private String url, TAG = "USER_DRAWER_ACTIVITY";
    private OkHttpClient client;
    private Request request;


    // Feedback dilaog Controls
    private AlertDialog feedbackDialog;
    private AppCompatButton dialog_feedbackDoneBN;
    private AppCompatTextView dialog_feedbackQuestionTV;
    private AppCompatRatingBar dialog_feedbackRB;
    private AppCompatEditText dialog_feebackCommentET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_drawer);

        bindControls();
        bindListeners();
        toolbarSetting();
        hitApiGetAllParlours();
        setUpHomePage();
        checkIfRatingWasGiven();
    }

    private void checkIfRatingWasGiven() {
        SharedPreferences sharedPreferences = getSharedPreferences("feedbackDialog_" + userEmail, MODE_PRIVATE);
        String email = sharedPreferences.getString("userEmail", null);
        if (email != null && email.equalsIgnoreCase(userEmail)) {
            parlourName = sharedPreferences.getString("parlourName", null);
            parlourId = sharedPreferences.getString("parlourId", null);
            bookingId = sharedPreferences.getString("bookingId", null);
            feedBackDialog();
        }
    }

    private void hitApiFeedback() {
        url = getString(R.string.url) + "feedbacks";

        client = new OkHttpClient.Builder()
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("parlourid", parlourId);
            jsonObject.put("bookingid", bookingId);
            jsonObject.put("remarks", dialog_feebackCommentET.getText().toString());
            jsonObject.put("rating", dialog_feedbackRB.getRating());
        } catch (Exception e) {
            Log.e(TAG, "hitApiFeedback: " + e);
        }

        RequestBody body = RequestBody.create( JSON, jsonObject.toString() );

        request = new Request.Builder()
                .url( url )
                .header("Authorization", "Bearer " + getToken())
                .post(body)
                .build();

        client.newCall( request ).enqueue( new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UserDrawerActivity.this,"Network Error", Toast.LENGTH_LONG).show();
                    }
                });
                Log.e(TAG, "hitApiFeedback: onFailure:" + e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()){
                    SharedPreferences.Editor editor = getSharedPreferences("feedbackDialog_" + userEmail, MODE_PRIVATE).edit();
                    editor.clear().apply();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UserDrawerActivity.this, "Thank you for your feedback.", Toast.LENGTH_LONG).show();
                            feedbackDialog.dismiss();
                        }
                    });

                } else {
                    Log.e(TAG, "hitApiFeedback: onResponse: " + response.code());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UserDrawerActivity.this, "Network error, try again later.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        } );
    }

    private void feedBackDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserDrawerActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_feedback, null);
        builder.setView(dialogView);

        bindDialogControls(dialogView);
        bindDialogListeners();
        setTextView();

        feedbackDialog = builder.create();
        feedbackDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        feedbackDialog.setCanceledOnTouchOutside(false);
        feedbackDialog.setCancelable(false);
        feedbackDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        feedbackDialog.show();
    }

    private void setTextView() {
        dialog_feedbackQuestionTV.setText("How was your experience with " + parlourName + "?");
    }

    private void bindDialogListeners() {
        dialog_feedbackDoneBN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hitApiFeedback();
            }
        });
    }

    private void bindDialogControls(View dialogView) {
        dialog_feedbackDoneBN = dialogView.findViewById(R.id.dialog_doneBN);
        dialog_feedbackRB = dialogView.findViewById(R.id.dialog_parlourRB);
        dialog_feedbackQuestionTV = dialogView.findViewById(R.id.dialog_questionTV);
        dialog_feebackCommentET = dialogView.findViewById(R.id.dialog_commentET);
    }

    private String getToken(){
        SharedPreferences sharedPreferences = getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        userEmail = sharedPreferences.getString("userName", null);
        return sharedPreferences.getString("access_token", null);
    }

    private void hitApiGetAllParlours() {
        url = getString(R.string.url) + "parlours";

        client = new OkHttpClient.Builder()
                .build();

        request = new Request.Builder()
                .url( url )
                .header("Authorization", "Bearer " + getToken())
                .get()
                .build();
        client.newCall( request ).enqueue( new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UserDrawerActivity.this,"Network Error", Toast.LENGTH_LONG).show();
                    }
                });
                Log.e(TAG, "hitApiGetAllParlours: onFailure: " + e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.code() == 200){
                    setUpSearhBarList(response);
                } else {
                    Log.e(TAG, "hitApiGetAllParlours : onResponse: " + response.code());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UserDrawerActivity.this, "Network error, try again later.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        } );
    }

    private void setUpSearhBarList(Response response) {
        try {
            allModelList = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(response.body().string());
            for (int i = 0; i < jsonArray.length(); i++) {
                allModelList.add(new ParlourModel(
                        jsonArray.getJSONObject(i).getString("Id"),
                        jsonArray.getJSONObject(i).getString("FullName"),
                        jsonArray.getJSONObject(i).getDouble("Rating"),
                        jsonArray.getJSONObject(i).getString("PhoneNumber"),
                        jsonArray.getJSONObject(i).getDouble("Latitude"),
                        jsonArray.getJSONObject(i).getDouble("Longitude")
                ));
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SearchAutoCompleteAdapter adapter = new SearchAutoCompleteAdapter(UserDrawerActivity.this, R.layout.item_search_bar, allModelList);
                    toolbarSearchTV.setAdapter(adapter);
                    toolbarSearchTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Toast.makeText(UserDrawerActivity.this, allModelList.get(position).getParlourName()+"", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(UserDrawerActivity.this, "Network error, try again later.", Toast.LENGTH_LONG).show();
                }
            });
            Log.e(TAG, "castParloursToList: " + e);
        }
    }

//    private void setUpSearhBarList() {
//        allModelList = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            allModelList.add(new ParlourModel(
//                    i,
//                    "All " + i,
//                    (double) i,
//                    String.valueOf(i)
//            ));
//        }
//        SearchAutoCompleteAdapter adapter = new SearchAutoCompleteAdapter(UserDrawerActivity.this, R.layout.item_search_bar, allModelList);
//        toolbarSearchTV.setAdapter(adapter);
//        toolbarSearchTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(UserDrawerActivity.this, allModelList.get(position).getParlourName()+"", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void setUpHomePage() {
        Boolean refreshFragment;
        try {
            refreshFragment = getIntent().getExtras().getBoolean("refresh", false);
        } catch (Exception e) {
            refreshFragment = false;
            Log.e(TAG, "setUpHomePage: " + e);
        }
        if (refreshFragment) {
            navigationView.setCheckedItem(R.id.nav_appointment);
            setUpFragment(new UserAppointmentFragment(), "Appointment");
        }
        else {
            navigationView.setCheckedItem(R.id.nav_home);
            setUpFragment(new UserHomeFragment(), "Home");
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
        toolbarSearchTV.setOnTouchListener(this);
    }

    private void bindControls() {
        toolbar = findViewById(R.id.toolbar);
        toolbarTitleTV = toolbar.findViewById(R.id.toolbarTitleTV);
        toolbarSearchTV = toolbar.findViewById(R.id.toolbarSearchTV);

        drawerLayout = findViewById(R.id.user_drawerDL);
        navigationView = findViewById(R.id.user_drawerNV);

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
        if (toolbarSearchTV.getVisibility() == View.VISIBLE) {
            hideAndShowIcons(true);
            toolbarSearchTV.setVisibility(View.GONE);
            toolbarTitleTV.setVisibility(View.VISIBLE);
        } else if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            finishAffinity();
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
                    Intent intent = new Intent(getApplicationContext(), FiltersActivity.class);
                    intent.putExtra("list", (ArrayList<ParlourModel>) allModelList);
                    startActivity(intent);
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
        } else if (id == R.id.action_cart) {
            startActivity(new Intent(UserDrawerActivity.this, CartActivity.class));
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
        } else if (id == R.id.nav_logout) {
            logout();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        setUpFragment(fragment, toolbarTitle);
        hideAndShowIcons(value);
        return true;
    }

    private void logout() {
        SharedPreferences.Editor editor = getSharedPreferences("userDetails", MODE_PRIVATE).edit();
        editor.clear().apply();
        startActivity(new Intent(UserDrawerActivity.this, LoginActivity.class));
    }
}
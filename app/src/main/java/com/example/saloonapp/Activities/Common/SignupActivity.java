package com.example.saloonapp.Activities.Common;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;

import com.example.saloonapp.Adapters.Common.SignupViewPagerAdapter;
import com.example.saloonapp.Fragments.Parlour.ParlourSignupFragment;
import com.example.saloonapp.Fragments.User.UserSignupFragment;
import com.example.saloonapp.R;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AppCompatImageView backIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        bindControls();
        bindListeners();
        setUpTabs();
    }

    private void bindListeners() {
        backIV.setOnClickListener(this);
    }

    private void bindControls() {
        tabLayout = findViewById(R.id.signup_TL);
        viewPager = findViewById(R.id.signup_VP);
        backIV = findViewById(R.id.signup_backIV);
    }

    private void setUpTabs() {
        SignupViewPagerAdapter adapter = new SignupViewPagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new UserSignupFragment(), "As User");
        adapter.AddFragment(new ParlourSignupFragment(), "As Parlour");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onClick(View v) {
        if (v == backIV) {
            onBackPressed();
        }
    }
}

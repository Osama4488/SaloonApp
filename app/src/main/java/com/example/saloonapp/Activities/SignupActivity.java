package com.example.saloonapp.Activities;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.saloonapp.Adapters.SignupViewPagerAdapter;
import com.example.saloonapp.Fragments.ParlourSignupFragment;
import com.example.saloonapp.Fragments.UserSignupFragment;
import com.example.saloonapp.R;

public class SignupActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        bindControls();
        setUpTabs();
    }

    private void bindControls() {
        tabLayout = findViewById(R.id.signup_TL);
        viewPager = findViewById(R.id.signup_VP);
    }

    private void setUpTabs() {
        SignupViewPagerAdapter adapter = new SignupViewPagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new UserSignupFragment(), "As User");
        adapter.AddFragment(new ParlourSignupFragment(), "As Parlour");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}

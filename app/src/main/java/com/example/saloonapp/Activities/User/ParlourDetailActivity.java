package com.example.saloonapp.Activities.User;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.example.saloonapp.Adapters.User.ParlourDetailViewPagerAdapter;
import com.example.saloonapp.Fragments.User.ParlourExpertFragment;
import com.example.saloonapp.Fragments.User.ParlourInfoFragment;
import com.example.saloonapp.Fragments.User.ParlourServiceFragment;
import com.example.saloonapp.Models.ExpertsModel;
import com.example.saloonapp.Models.ParlourServicesModel;
import com.example.saloonapp.Models.ServicesModel;
import com.example.saloonapp.Models.SubServicesModel;
import com.example.saloonapp.R;

import java.util.ArrayList;
import java.util.List;

public class ParlourDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AppCompatTextView parlourNameTV;
    private AppCompatImageView backIV;
    private List<ExpertsModel> expertsModelList;
    private List<ParlourServicesModel> parlourServicesModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parlour_detail);

        bindControls();
        bindListeners();
        setUpServicesList();
        setUpExpertList();
        setUpTabs();
    }

    private void bindControls() {
        tabLayout = findViewById(R.id.parlour_detail_TL);
        viewPager = findViewById(R.id.parlour_detail_VP);
        backIV = findViewById(R.id.parlour_detail_backIV);
        parlourNameTV = findViewById(R.id.parlour_detail_parlourNameTV);
    }

    private void bindListeners() {
        backIV.setOnClickListener(this);
    }

    private void setUpServicesList() {
        parlourServicesModelList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            List<SubServicesModel> tempList = new ArrayList<>();
            for (int j = 0; j <= i; j++) {
                tempList.add(new SubServicesModel(
                        j,
                        "Sub Service " + j,
                        "Sub Service Description " + j,
                        j * 100
                ));
            }
            parlourServicesModelList.add(new ParlourServicesModel(
                    new ServicesModel(String.valueOf(i), "Service " + i),
                    tempList
            ));
        }
    }

    private void setUpExpertList(){
        expertsModelList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            expertsModelList.add(new ExpertsModel(
                    "",
                    "Expert Name " + i,
                    "Expert Expertise " + i,
                    String.valueOf(i)
            ));
        }
    }

    private void setUpTabs() {
        ParlourDetailViewPagerAdapter adapter = new ParlourDetailViewPagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new ParlourInfoFragment(), "Information");
        adapter.AddFragment(new ParlourServiceFragment(parlourServicesModelList), "Services");
        adapter.AddFragment(new ParlourExpertFragment(expertsModelList), "Experts");
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

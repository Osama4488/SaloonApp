package com.example.saloonapp.Adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class SignupViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragmentsList =  new ArrayList<>();
    private List<String> fragmentsNameList =  new ArrayList<>();

    public SignupViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentsList.get(i);
    }

    @Override
    public int getCount() {
        return fragmentsNameList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentsNameList.get(position);
    }

    public void AddFragment(Fragment fragment, String title){
        fragmentsList.add(fragment);
        fragmentsNameList.add(title);
    }
}

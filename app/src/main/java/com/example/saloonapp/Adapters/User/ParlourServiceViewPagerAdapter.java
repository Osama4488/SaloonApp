package com.example.saloonapp.Adapters.User;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.saloonapp.Models.FragmentModel;

import java.util.ArrayList;
import java.util.List;

public class ParlourServiceViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<FragmentModel> fragmentsList = new ArrayList<>();

    public ParlourServiceViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentsList.get(i).getFragment();
    }

    @Override
    public int getCount() {
        return fragmentsList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentsList.get(position).getFragmentName();
    }

    public void AddFragment(Fragment fragment, String title) {
        fragmentsList.add(new FragmentModel(
                fragment,
                title
        ));
    }
}


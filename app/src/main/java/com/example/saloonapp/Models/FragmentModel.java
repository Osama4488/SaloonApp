package com.example.saloonapp.Models;

import android.support.v4.app.Fragment;

public class FragmentModel {
    private Fragment fragment;
    private String fragmentName;

    public FragmentModel(Fragment fragment, String fragmentName) {
        this.fragment = fragment;
        this.fragmentName = fragmentName;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public String getFragmentName() {
        return fragmentName;
    }
}

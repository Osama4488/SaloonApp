package com.example.saloonapp.Fragments.User;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.saloonapp.Adapters.User.ParlourServiceViewPagerAdapter;
import com.example.saloonapp.Models.ParlourServicesModel;
import com.example.saloonapp.Models.ServicesModel;
import com.example.saloonapp.Models.SubServicesModel;
import com.example.saloonapp.R;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class ParlourServiceFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private List<ParlourServicesModel> parlourServicesModelList;

    @SuppressLint("ValidFragment")
    public ParlourServiceFragment(List<ParlourServicesModel> parlourServicesModelList) {
        this.parlourServicesModelList = parlourServicesModelList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_parlour_service, container, false);

        bindControls(view);
        setUpTabs();
        return view;
    }

    private void setUpTabs() {
        ParlourServiceViewPagerAdapter adapter = new ParlourServiceViewPagerAdapter(getActivity().getSupportFragmentManager());
        for (int i = 0; i < parlourServicesModelList.size(); i++){
            adapter.AddFragment(
                    new ParlourSubServiceFragment(parlourServicesModelList.get(i).getSubServiceList()),
                    parlourServicesModelList.get(i).getService().getServiceName()
            );
        }
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void bindControls(View view) {
        tabLayout = view.findViewById(R.id.parlour_service_frag_TL);
        viewPager = view.findViewById(R.id.parlour_service_frag_VP);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}

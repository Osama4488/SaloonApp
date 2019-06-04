package com.example.saloonapp.Fragments.User;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.saloonapp.Adapters.User.ParlourSubServiceRecyclerViewAdapter;
import com.example.saloonapp.Models.SubServicesModel;
import com.example.saloonapp.R;

import java.util.List;

@SuppressLint("ValidFragment")
public class ParlourSubServiceFragment extends Fragment {

    private RecyclerView subServiceRV;
    private List<SubServicesModel> subServicesModelList;

    @SuppressLint("ValidFragment")
    public ParlourSubServiceFragment(List<SubServicesModel> subServicesModelList) {
        this.subServicesModelList = subServicesModelList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parlour_sub_service, container, false);
        bindControls(view);
        setupList();
        return view;
    }

    private void setupList() {
        ParlourSubServiceRecyclerViewAdapter adapter = new ParlourSubServiceRecyclerViewAdapter(getActivity(), subServicesModelList);
        subServiceRV.setHasFixedSize(true);
        subServiceRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        subServiceRV.setAdapter(adapter);
    }

    private void bindControls(View view) {
        subServiceRV = view.findViewById(R.id.parlour_subService_RV);
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

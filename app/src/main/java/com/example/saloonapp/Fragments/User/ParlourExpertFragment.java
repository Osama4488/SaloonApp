package com.example.saloonapp.Fragments.User;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.saloonapp.Adapters.User.ParlourExpertsRecyclerViewAdapter;
import com.example.saloonapp.Models.ExpertsModel;
import com.example.saloonapp.R;

import java.util.List;

@SuppressLint("ValidFragment")
public class ParlourExpertFragment extends Fragment {

    private RecyclerView expertsRV;
    private List<ExpertsModel> expertsModelList;

    @SuppressLint("ValidFragment")
    public ParlourExpertFragment(List<ExpertsModel> expertsModelList) {
        this.expertsModelList = expertsModelList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parlour_expert, container, false);
        bindControls(view);
        setUpList();
        return view;
    }

    private void bindControls(View view) {
        expertsRV = view.findViewById(R.id.parlour_expert_frag_RV);
    }

    private void setUpList() {
        ParlourExpertsRecyclerViewAdapter adapter = new ParlourExpertsRecyclerViewAdapter(getActivity(), expertsModelList);
        expertsRV.setHasFixedSize(true);
        expertsRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        expertsRV.setAdapter(adapter);
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

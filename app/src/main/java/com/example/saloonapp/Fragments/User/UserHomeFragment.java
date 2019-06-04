package com.example.saloonapp.Fragments.User;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.saloonapp.Activities.User.ListOfParloursActivity;
import com.example.saloonapp.Adapters.User.ParlourRecyclerViewAdapter;
import com.example.saloonapp.Models.ParlourModel;
import com.example.saloonapp.R;

import java.util.ArrayList;
import java.util.List;


public class UserHomeFragment extends Fragment implements View.OnClickListener {

    private AppCompatTextView nearestSeeAllTV, recommendedSeeAllTV, allSeeAllTV;
    private RecyclerView nearestRV, recommendedRV, allRV;
    private List<ParlourModel> nearestModelList, recommendedModelList, allModelList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_home, container, false);

        bindControls(view);
        bindListeners();
        dummyList();
        return view;
    }

    private void bindControls(View view) {
        nearestSeeAllTV = view.findViewById(R.id.user_home_nearestParlourSeeAllTV);
        recommendedSeeAllTV = view.findViewById(R.id.user_home_recommendedParlourSeeAllTV);
        allSeeAllTV = view.findViewById(R.id.user_home_allParlourSeeAllTV);

        nearestRV = view.findViewById(R.id.user_home_nearestParlourRV);
        recommendedRV = view.findViewById(R.id.user_home_recommendedParlourRV);
        allRV = view.findViewById(R.id.user_home_allParlourRV);
    }

    private void bindListeners() {
        nearestSeeAllTV.setOnClickListener(this);
        recommendedSeeAllTV.setOnClickListener(this);
        allSeeAllTV.setOnClickListener(this);
    }

    private void dummyList() {
        nearestModelList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            nearestModelList.add(new ParlourModel(
                    i,
                    "Nearest " + i,
                    (double) i,
                    String.valueOf(i)
            ));
        }

        recommendedModelList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            recommendedModelList.add(new ParlourModel(
                    i,
                    "Recommeded " + i,
                    (double) i,
                    String.valueOf(i)
            ));
        }

        allModelList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            allModelList.add(new ParlourModel(
                    i,
                    "All " + i,
                    (double) i,
                    String.valueOf(i)
            ));
        }

        setUpList(nearestRV, nearestModelList);
        setUpList(recommendedRV, recommendedModelList);
        setUpList(allRV, allModelList);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setUpList(RecyclerView recyclerView, List<ParlourModel> list) {
        ArrayList<ParlourModel> tempList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            tempList.add(list.get(i));
        }
        ParlourRecyclerViewAdapter adapter = new ParlourRecyclerViewAdapter(getActivity(), tempList, R.layout.item_parlour_horizontal);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        if (v == nearestSeeAllTV) {
            startParlourDetailActivity("Nearest", nearestModelList);
        } else if (v == recommendedSeeAllTV) {
            startParlourDetailActivity("Recommended", recommendedModelList);
        } else if (v == allSeeAllTV) {
            startParlourDetailActivity("All", allModelList);
        }
    }

    private void startParlourDetailActivity(String title, List<ParlourModel> list) {
        Intent intent = new Intent(getActivity(), ListOfParloursActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("list", (ArrayList<ParlourModel>) list);
        startActivity(intent);
    }
}

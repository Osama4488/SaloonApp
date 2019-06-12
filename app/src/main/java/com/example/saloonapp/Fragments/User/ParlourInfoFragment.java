package com.example.saloonapp.Fragments.User;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.saloonapp.Adapters.User.ParlourTimingsRecyclerViewAdapter;
import com.example.saloonapp.Models.ParlourInformationModel;
import com.example.saloonapp.R;

@SuppressLint("ValidFragment")
public class ParlourInfoFragment extends Fragment {

    private ParlourInformationModel parlourInformationModel;
    private AppCompatTextView parlourName, parlourEmail, parlourNumber;
    private AppCompatRatingBar parlourRB;
    private RecyclerView parlourTimingRV;

    @SuppressLint("ValidFragment")
    public ParlourInfoFragment(ParlourInformationModel parlourInformationModel) {
        this.parlourInformationModel = parlourInformationModel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parlour_info, container, false);

        bindControls(view);
        showParlourDetails();
        setUpDaysList();
        return view;
    }

    private void bindControls(View view) {
        parlourEmail = view.findViewById(R.id.appointment_parlourEmailTV);
        parlourName = view.findViewById(R.id.parlour_info_frag_parlourNameTV);
        parlourNumber = view.findViewById(R.id.appointment_parlourNumberTV);
        parlourRB = view.findViewById(R.id.appointment_parlourRB);
        parlourTimingRV = view.findViewById(R.id.appointment_appointmentDetailsRV);
    }

    private void showParlourDetails() {
        parlourName.setText(parlourInformationModel.getParlourName());
        parlourEmail.setText(parlourInformationModel.getParlourEmail());
        parlourNumber.setText(parlourInformationModel.getParlourNumber());
        parlourRB.setRating(Float.valueOf(parlourInformationModel.getParlourRating()));
    }

    private void setUpDaysList() {
        ParlourTimingsRecyclerViewAdapter adapter = new ParlourTimingsRecyclerViewAdapter(getActivity(), parlourInformationModel.getParlourTimingsModelList());
        parlourTimingRV.setHasFixedSize(true);
        parlourTimingRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        parlourTimingRV.setAdapter(adapter);
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

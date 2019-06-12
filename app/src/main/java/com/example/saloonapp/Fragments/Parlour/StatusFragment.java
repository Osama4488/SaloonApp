package com.example.saloonapp.Fragments.Parlour;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.saloonapp.Adapters.Parlour.StatusRecyclerViewAdapter;
import com.example.saloonapp.Models.BookingOrAppointmentModel;
import com.example.saloonapp.R;

import java.util.List;

@SuppressLint("ValidFragment")
public class StatusFragment extends Fragment {

    private List<BookingOrAppointmentModel> modelList;
    private RecyclerView statusRV;
    private AppCompatTextView titleTV;
    private String status;

    public StatusFragment(List<BookingOrAppointmentModel> modelList, String status) {
        this.modelList = modelList;
        this.status = status;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);

        bindControls(view);
        if (modelList.size() == 0) {
            statusRV.setVisibility(View.GONE);
            titleTV.setVisibility(View.VISIBLE);
            titleTV.setText("no " + status + " bookings");
        } else {
            statusRV.setVisibility(View.VISIBLE);
            titleTV.setVisibility(View.GONE);
            setUpList();
        }
        return view;
    }

    private void setUpList() {
        StatusRecyclerViewAdapter adapter = new StatusRecyclerViewAdapter(getActivity(), status, modelList);
        statusRV.setHasFixedSize(true);
        statusRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        statusRV.setAdapter(adapter);
    }

    private void bindControls(View view) {
        statusRV = view.findViewById(R.id.booking_status_RV);
        titleTV = view.findViewById(R.id.booking_status_titleTV);
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

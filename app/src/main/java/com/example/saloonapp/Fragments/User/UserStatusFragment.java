package com.example.saloonapp.Fragments.User;

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

import com.example.saloonapp.Adapters.User.UserStatusRecyclerViewAdapter;
import com.example.saloonapp.Models.BookingOrAppointmentModel;
import com.example.saloonapp.R;

import java.util.List;

@SuppressLint("ValidFragment")
public class UserStatusFragment extends Fragment {

    private String status;
    private List<BookingOrAppointmentModel> bookingOrAppointmentModelsList;
    private RecyclerView statusRV;
    private AppCompatTextView titleTV;

    public UserStatusFragment(List<BookingOrAppointmentModel> bookingOrAppointmentModelsList, String status) {
        this.status = status;
        this.bookingOrAppointmentModelsList = bookingOrAppointmentModelsList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_status, container, false);

        bindControls(view);
        if (bookingOrAppointmentModelsList.size() == 0) {
            statusRV.setVisibility(View.GONE);
            titleTV.setVisibility(View.VISIBLE);
            if (status.equals("Scheduled")) {
                titleTV.setText("no " + status + " Appointments");
            } else {
                titleTV.setText("no " + status);
            }
        } else {
            statusRV.setVisibility(View.VISIBLE);
            titleTV.setVisibility(View.GONE);
            setUpList();
        }
        return view;
    }

    private void bindControls(View view) {
        statusRV = view.findViewById(R.id.appointment_status_RV);
        titleTV = view.findViewById(R.id.appointment_status_titleTV);
    }

    private void setUpList() {
        UserStatusRecyclerViewAdapter adapter = new UserStatusRecyclerViewAdapter(getActivity(), status, bookingOrAppointmentModelsList);
        statusRV.setHasFixedSize(true);
        statusRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        statusRV.setAdapter(adapter);
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

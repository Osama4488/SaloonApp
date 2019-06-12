package com.example.saloonapp.Fragments.User;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.saloonapp.Adapters.User.AppointmentViewPagerAdapter;
import com.example.saloonapp.Models.BookingOrAppointmentModel;
import com.example.saloonapp.R;

import java.util.ArrayList;
import java.util.List;

public class UserAppointmentFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private List<BookingOrAppointmentModel> bookingOrAppointmentModelList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointment, container, false);

        bindControls(view);
        dummyList();
        setUpTabs();
        return view;
    }

    private void bindControls(View view) {
        tabLayout = view.findViewById(R.id.appointment_frag_TL);
        viewPager = view.findViewById(R.id.appointment_frag_VP);
    }

    private void dummyList() {
        bookingOrAppointmentModelList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            bookingOrAppointmentModelList.add(new BookingOrAppointmentModel(
                    String.valueOf(i),
                    String.valueOf(i),
                    "Parlour Name " + i,
                    "2019-06-03T17:15:00"
            ));
        }
    }

    private void setUpTabs() {
        AppointmentViewPagerAdapter adapter = new AppointmentViewPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.AddFragment(new UserStatusFragment(bookingOrAppointmentModelList, "Scheduled"), "Scheduled");
        adapter.AddFragment(new UserStatusFragment(new ArrayList<BookingOrAppointmentModel>(), "History"), "History");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
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

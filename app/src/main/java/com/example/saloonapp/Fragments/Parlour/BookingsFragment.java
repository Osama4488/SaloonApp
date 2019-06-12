package com.example.saloonapp.Fragments.Parlour;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.saloonapp.Adapters.Parlour.BookingViewPagerAdapter;
import com.example.saloonapp.Models.BookingOrAppointmentModel;
import com.example.saloonapp.R;

import java.util.ArrayList;
import java.util.List;

public class BookingsFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private List<BookingOrAppointmentModel> modelList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookings, container, false);

        bindControls(view);
        dummyList();
        setUpTabs();
        return view;
    }

    private void bindControls(View view) {
        tabLayout = view.findViewById(R.id.booking_frag_TL);
        viewPager = view.findViewById(R.id.booking_frag_VP);
    }

    private void dummyList() {
        modelList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            modelList.add(new BookingOrAppointmentModel(
                    String.valueOf(i),
                    String.valueOf(i),
                    "User Name " + i,
                    "2019-06-03T17:15:00"
            ));
        }
    }

    private void setUpTabs() {
        BookingViewPagerAdapter adapter = new BookingViewPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.AddFragment(new StatusFragment(modelList, "Pending"), "Pending");
        adapter.AddFragment(new StatusFragment(modelList, "Accepted"), "Accepted");
        adapter.AddFragment(new StatusFragment(new ArrayList<BookingOrAppointmentModel>(), "Rejected"), "Rejected");
        adapter.AddFragment(new StatusFragment(modelList, "Completed"), "Completed");
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

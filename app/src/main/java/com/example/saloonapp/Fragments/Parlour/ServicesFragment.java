package com.example.saloonapp.Fragments.Parlour;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.CycleInterpolator;

import com.example.saloonapp.Adapters.Parlour.ExpertsRecyclerViewAdapter;
import com.example.saloonapp.Adapters.Parlour.ServicesRecyclerViewAdapter;
import com.example.saloonapp.Models.ServicesModel;
import com.example.saloonapp.R;

import java.util.ArrayList;
import java.util.List;

public class ServicesFragment extends Fragment implements View.OnClickListener {

    public FloatingActionButton addServiceFAB;
    private List<ServicesModel> servicesModelList;
    public RecyclerView serviceRV;
    private AppCompatTextView titleTV;

    // Dialog Controls
    private AlertDialog addServiceDialog;
    private AppCompatButton dialog_addServiceBN;
    private TextInputLayout dialog_serviceNameTIL;
    private AppCompatEditText dialog_serviceNameET;
    private AppCompatImageButton dialog_closeIB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_services, container, false);

        bindControls(view);
        bindListeners();
        dummyList();

        return view;
    }

    private void bindListeners() {
        addServiceFAB.setOnClickListener(this);
    }

    private void dummyList() {
        servicesModelList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            servicesModelList.add(new ServicesModel(
                    i,
                    "Service " + i
            ));
        }
        setUpList();
    }

    private void setUpList() {
        if (servicesModelList.size() == 0) {
            controlsVisibility(View.VISIBLE);
        } else {
            controlsVisibility(View.GONE);
            ServicesRecyclerViewAdapter adapter = new ServicesRecyclerViewAdapter(getActivity(), servicesModelList, serviceRV, titleTV);
            serviceRV.setHasFixedSize(true);
            serviceRV.setLayoutManager(new LinearLayoutManager(getActivity()));
            serviceRV.setAdapter(adapter);
        }
    }

    private void controlsVisibility(int visibliltiy) {
        if (View.VISIBLE == visibliltiy) {
            titleTV.setVisibility(View.VISIBLE);
            serviceRV.setVisibility(View.GONE);
        } else {
            titleTV.setVisibility(View.GONE);
            serviceRV.setVisibility(View.VISIBLE);
        }
    }

    private void bindControls(View view) {
        addServiceFAB = view.findViewById(R.id.services_frag_addServiceFAB);
        serviceRV = view.findViewById(R.id.services_frag_RV);
        titleTV = view.findViewById(R.id.services_frag_titleTV);
    }

    @Override
    public void onClick(View v) {
        if (v == addServiceFAB) {
            showAddServiceDialog();
        } else if (v == dialog_closeIB) {
            addServiceDialog.dismiss();
        } else if (v == dialog_addServiceBN) {
            addService();
        }
    }

    private void addService() {
        String serviceName = dialog_serviceNameET.getText().toString();
        if (checkIsNullOrEmpty(serviceName)) {
            dialog_serviceNameTIL.setErrorEnabled(true);
            dialog_serviceNameTIL.setError("Can not be empty");
            animateDialog(addServiceDialog);
        } else if (!(dialog_serviceNameTIL.isErrorEnabled())) {
            servicesModelList.add(new ServicesModel(
                    (int) Math.random(),
                    serviceName
            ));
            setUpList();
            addServiceDialog.dismiss();
        }
    }

    private void animateDialog(AlertDialog dialog) {
        dialog.getWindow()
                .getDecorView()
                .animate()
                .translationX(16f)
                .setInterpolator(new CycleInterpolator(7f));
    }

    private void showAddServiceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_service, null);
        builder.setView(dialogView);

        bindDialogControls(dialogView);
        bindDialogListeners();
        setDialogEdittextError();

        addServiceDialog = builder.create();
        addServiceDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addServiceDialog.setCanceledOnTouchOutside(true);
        addServiceDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addServiceDialog.show();
    }

    private void setDialogEdittextError() {
        dialog_serviceNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String serviceName = dialog_serviceNameET.getText().toString();
                if (!(checkIsNullOrEmpty(serviceName))) {
                    dialog_serviceNameTIL.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean checkIsNullOrEmpty(String value) {
        if (value.isEmpty() || value.equals("")){
            return true;
        } else {
            return false;
        }
    }

    private void bindDialogListeners() {
        dialog_closeIB.setOnClickListener(this);
        dialog_addServiceBN.setOnClickListener(this);
    }

    private void bindDialogControls(View dialogView) {
        dialog_serviceNameTIL = dialogView.findViewById(R.id.dialog_serviceNameTIL);
        dialog_serviceNameET = dialogView.findViewById(R.id.dialog_serviceNameET);
        dialog_closeIB = dialogView.findViewById(R.id.dialog_closeIB);
        dialog_addServiceBN = dialogView.findViewById(R.id.dialog_addServiceBN);
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

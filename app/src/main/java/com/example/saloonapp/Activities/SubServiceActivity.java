package com.example.saloonapp.Activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.CycleInterpolator;

import com.example.saloonapp.Adapters.ExpertsRecyclerViewAdapter;
import com.example.saloonapp.Adapters.SubServicesRecyclerViewAdapter;
import com.example.saloonapp.Models.SubServicesModel;
import com.example.saloonapp.R;

import java.util.ArrayList;
import java.util.List;

public class SubServiceActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private AppCompatTextView toolbarTitleTV;

    private FloatingActionButton addSubServiceFAB;
    private RecyclerView subServicesRV;
    private List<SubServicesModel> subServicesModelList;

    // Dialog Controls
    private AlertDialog addSubServiceDialog;
    private AppCompatTextView dialog_subServiceTitleTV;
    private AppCompatButton dialog_addSubServiceBN;
    private TextInputLayout dialog_subServiceNameTIL, dialog_subServicePriceTIL, dialog_subServiceDescriptionTIL;
    private AppCompatEditText dialog_subServiceNameET, dialog_subServicePriceET, dialog_subServiceDescriptionET;
    private AppCompatImageButton dialog_closeIB;

    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_service);

        getValuesOnStart();
        bindControls();
        bindListeners();
        toolbarSetting();
        dummyList();
    }

    private void getValuesOnStart() {
        title = getIntent().getExtras().getString("serviceName" , "not found");
    }

    private void bindControls() {
        toolbar = findViewById(R.id.toolbar);
        toolbarTitleTV = toolbar.findViewById(R.id.toolbarTitleTV);

        addSubServiceFAB = findViewById(R.id.subService_addSubServiceFAB);
        subServicesRV = findViewById(R.id.subService_RV);
    }

    private void bindListeners() {
        addSubServiceFAB.setOnClickListener(this);
    }

    private void dummyList() {
        subServicesModelList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            subServicesModelList.add(new SubServicesModel(
                    i,
                    "Sub Service " + i,
                    "Description " + i,
                    i * 100
            ));
        }

        SubServicesRecyclerViewAdapter adapter = new SubServicesRecyclerViewAdapter(this, title, subServicesModelList);
        subServicesRV.setHasFixedSize(true);
        subServicesRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        subServicesRV.setAdapter(adapter);

    }

    private void toolbarSetting() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbarTitleTV.setText(title.toUpperCase());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == addSubServiceFAB) {
            showAddSubServiceDialog();
        } else if (v == dialog_closeIB) {
            addSubServiceDialog.dismiss();
        } else if (v == dialog_addSubServiceBN) {
            addSubService();
        }
    }

    private void addSubService() {
        String subServiceName = dialog_subServiceNameET.getText().toString();
        String subServicePrice = dialog_subServicePriceET.getText().toString();
        String subServiceDescription = dialog_subServiceDescriptionET.getText().toString();
        if (checkIsNullOrEmpty(subServiceName) || checkIsNullOrEmpty(subServicePrice) || checkIsNullOrEmpty(subServiceDescription)) {
            if (checkIsNullOrEmpty(subServiceName)) {
                dialog_subServiceNameTIL.setErrorEnabled(true);
                dialog_subServiceNameTIL.setError("Can not be empty");
            }
            if (checkIsNullOrEmpty(subServicePrice)) {
                dialog_subServicePriceTIL.setErrorEnabled(true);
                dialog_subServicePriceTIL.setError("Can not be empty");
            }
            if (checkIsNullOrEmpty(subServiceDescription)) {
                dialog_subServiceDescriptionTIL.setErrorEnabled(true);
                dialog_subServiceDescriptionTIL.setError("Can not be empty");
            }
            animateDialog(addSubServiceDialog);
        } else if (!(dialog_subServiceNameTIL.isErrorEnabled() && dialog_subServicePriceTIL.isErrorEnabled() && dialog_subServiceDescriptionTIL.isErrorEnabled())) {
            subServicesModelList.add(new SubServicesModel(
                    (int) Math.random(),
                    subServiceName,
                    subServiceDescription,
                    Integer.valueOf(subServicePrice)
            ));
            SubServicesRecyclerViewAdapter adapter = new SubServicesRecyclerViewAdapter(this, title, subServicesModelList);
            subServicesRV.setHasFixedSize(true);
            subServicesRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            subServicesRV.setAdapter(adapter);
            addSubServiceDialog.dismiss();
        }
    }

    private void animateDialog(AlertDialog dialog) {
        dialog.getWindow()
                .getDecorView()
                .animate()
                .translationX(16f)
                .setInterpolator(new CycleInterpolator(7f));
    }

    private void showAddSubServiceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_sub_service, null);
        builder.setView(dialogView);

        bindDialogControls(dialogView);
        bindDialogListeners();
        setDialogEdittextError();

        addSubServiceDialog = builder.create();
        addSubServiceDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addSubServiceDialog.setCanceledOnTouchOutside(true);
        addSubServiceDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addSubServiceDialog.show();
    }

    private void setDialogEdittextError() {
        dialog_subServiceNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String subServiceName = dialog_subServiceNameET.getText().toString();
                if (!(checkIsNullOrEmpty(subServiceName))) {
                    dialog_subServiceNameTIL.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        dialog_subServicePriceET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String subServicePrice = dialog_subServicePriceET.getText().toString();
                if (subServicePrice.equals("0")) {
                    dialog_subServicePriceTIL.setErrorEnabled(true);
                    dialog_subServicePriceTIL.setError("Price can not be '0'");
                } else {
                    if (!(checkIsNullOrEmpty(subServicePrice))) {
                        dialog_subServicePriceTIL.setErrorEnabled(false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        dialog_subServiceDescriptionET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String expertExperience = dialog_subServiceDescriptionET.getText().toString();
                if (!(checkIsNullOrEmpty(expertExperience))) {
                    dialog_subServiceDescriptionTIL.setErrorEnabled(false);
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
        dialog_addSubServiceBN.setOnClickListener(this);
    }

    private void bindDialogControls(View dialogView) {
        dialog_subServiceTitleTV = dialogView.findViewById(R.id.dialog_subServiceTitleTV);
        dialog_subServiceNameTIL = dialogView.findViewById(R.id.dialog_subServiceNameTIL);
        dialog_subServicePriceTIL = dialogView.findViewById(R.id.dialog_subServicePriceTIL);
        dialog_subServiceDescriptionTIL = dialogView.findViewById(R.id.dialog_subServiceDescriptionTIL);
        dialog_subServiceNameET = dialogView.findViewById(R.id.dialog_subServiceNameET);
        dialog_subServicePriceET = dialogView.findViewById(R.id.dialog_subServicePriceET);
        dialog_subServiceDescriptionET = dialogView.findViewById(R.id.dialog_subServiceDescriptionET);
        dialog_closeIB = dialogView.findViewById(R.id.dialog_closeIB);
        dialog_addSubServiceBN = dialogView.findViewById(R.id.dialog_addSubServiceBN);

        dialog_subServiceTitleTV.setText("Add " + title + " Service");
    }
}

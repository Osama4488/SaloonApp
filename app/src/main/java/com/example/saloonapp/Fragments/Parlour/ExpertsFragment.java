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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.CycleInterpolator;
import android.widget.Toast;

import com.example.saloonapp.Adapters.Parlour.ExpertsRecyclerViewAdapter;
import com.example.saloonapp.Models.ExpertsModel;
import com.example.saloonapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ExpertsFragment extends Fragment implements View.OnClickListener {

    public FloatingActionButton addExpertFAB;
    private List<ExpertsModel> expertsModelList;
    public RecyclerView expertsRV;
    private AppCompatTextView titleTV;
    private String strUrl,strServerResponse,strToken,TAG = "Experts";

    // Dialog Controls
    private AlertDialog addexpertDialog;
    private AppCompatButton dialog_addExpertBN;
    private TextInputLayout dialog_expertNameTIL, dialog_expertExpertiseTIL, dialog_expertExperienceTIL;
    private AppCompatEditText dialog_expertNameET, dialog_expertExpertiseET, dialog_expertExperienceET;
    private AppCompatImageButton dialog_closeIB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_experts, container, false);

        bindControls(view);
        bindListeners();
       // dummyList();

        strToken = getResources().getString( R.string.parlourToken );
        getExperts();

        return view;
    }

    private void bindListeners() {
        addExpertFAB.setOnClickListener(this);
    }

//    private void dummyList() {
//        expertsModelList = new ArrayList<>();
//        for (int i = 0; i < 2; i++) {
//            expertsModelList.add(new ExpertsModel(
//                    i,
//                    "Expert " + i,
//                    "Expertise " + i,
//                    String.valueOf(i)
//            ));
//        }
//        setUpList();
//    }


    private void setUpList() {
        if (expertsModelList.size() == 0) {
            controlsVisibility(View.VISIBLE);
        } else {
            controlsVisibility(View.GONE);
            ExpertsRecyclerViewAdapter adapter = new ExpertsRecyclerViewAdapter(getActivity(), expertsModelList, expertsRV, titleTV);
            expertsRV.setHasFixedSize(true);
            expertsRV.setLayoutManager(new LinearLayoutManager(getActivity()));
            expertsRV.setAdapter(adapter);
        }
    }

    public void getExperts(){
        strUrl = getResources().getString( R.string.url )+"experts";


        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(strUrl)
                .addHeader( "key",strToken )
                .build();


        client.newCall( request ).enqueue( new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                if (response.isSuccessful()) {
                    strServerResponse = response.body().string();
                    //Log.e( TAG, "onResponse: "+strServerResponse );

                    castExperts(strServerResponse);

                }else{
                    getActivity().runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText( getActivity(),"Network error, try again later",Toast.LENGTH_LONG).show();
                        }
                    } );
                    Log.e( TAG, "onResponse: "+strServerResponse );
                }
            }
        } );

    }

    private void castExperts(String strServerResponse) {

        expertsModelList = new ArrayList <>();

        try {
            JSONArray jsonArray = new JSONArray( strServerResponse );
            for (int i = 0; i < jsonArray.length(); i++) {

                expertsModelList.add( new ExpertsModel(
                        jsonArray.getJSONObject( i ).getString( "Id" ),
                        jsonArray.getJSONObject( i ).getString( "Name" ),
                        jsonArray.getJSONObject( i ).getString( "Expertise" ),
                        jsonArray.getJSONObject( i ).getString( "Experience" )
                ) );

            }

            getActivity().runOnUiThread( new Runnable() {
                @Override
                public void run() {
                    setUpList();
                }
            } );


            Log.e( TAG, "castExperts: " + jsonArray );
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void controlsVisibility(int visibliltiy) {
        if (View.VISIBLE == visibliltiy) {
            titleTV.setVisibility(View.VISIBLE);
            expertsRV.setVisibility(View.GONE);
        } else {
            titleTV.setVisibility(View.GONE);
            expertsRV.setVisibility(View.VISIBLE);
        }
    }

    private void bindControls(View view) {
        addExpertFAB = view.findViewById(R.id.subService_addSubServiceFAB);
        expertsRV = view.findViewById(R.id.subService_RV);
        titleTV = view.findViewById(R.id.experts_frag_titleTV);
    }

    @Override
    public void onClick(View v) {
        if (v == addExpertFAB) {
            showAddExpertDialog();
        } else if (v == dialog_closeIB) {
            addexpertDialog.dismiss();
        } else if (v == dialog_addExpertBN) {
            addService();
        }
    }

    private void addService() {
        String expertName = dialog_expertNameET.getText().toString();
        String expertExpertise = dialog_expertExpertiseET.getText().toString();
        String expertExperience = dialog_expertExperienceET.getText().toString();
        if (checkIsNullOrEmpty(expertName) || checkIsNullOrEmpty(expertExpertise) || checkIsNullOrEmpty(expertExperience)) {
            if (checkIsNullOrEmpty(expertName)) {
                dialog_expertNameTIL.setErrorEnabled(true);
                dialog_expertNameTIL.setError("Can not be empty");
            }
            if (checkIsNullOrEmpty(expertExpertise)) {
                dialog_expertExpertiseTIL.setErrorEnabled(true);
                dialog_expertExpertiseTIL.setError("Can not be empty");
            }
            if (checkIsNullOrEmpty(expertExperience)) {
                dialog_expertExperienceTIL.setErrorEnabled(true);
                dialog_expertExperienceTIL.setError("Can not be empty");
            }
            animateDialog(addexpertDialog);
        } else if (!(dialog_expertNameTIL.isErrorEnabled() && dialog_expertExpertiseTIL.isErrorEnabled() && dialog_expertExperienceTIL.isErrorEnabled())) {
            expertsModelList.add(new ExpertsModel(
                    null,
                    expertName,
                    expertExpertise,
                    expertExperience
            ));
            setUpList();
            addexpertDialog.dismiss();
        }
    }

    private void animateDialog(AlertDialog dialog) {
        dialog.getWindow()
                .getDecorView()
                .animate()
                .translationX(16f)
                .setInterpolator(new CycleInterpolator(7f));
    }

    private void showAddExpertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_expert, null);
        builder.setView(dialogView);

        bindDialogControls(dialogView);
        bindDialogListeners();
        setDialogEdittextError();

        addexpertDialog = builder.create();
        addexpertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addexpertDialog.setCanceledOnTouchOutside(true);
        addexpertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addexpertDialog.show();
    }

    private void setDialogEdittextError() {
        dialog_expertNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String expertName = dialog_expertNameET.getText().toString();
                if (!(checkIsNullOrEmpty(expertName))) {
                    dialog_expertNameTIL.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        dialog_expertExpertiseET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String expertExpertise = dialog_expertExpertiseET.getText().toString();
                if (!(checkIsNullOrEmpty(expertExpertise))) {
                    dialog_expertExpertiseTIL.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        dialog_expertExperienceET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String expertExperience = dialog_expertExperienceET.getText().toString();
                if (expertExperience.equals("0")) {
                    dialog_expertExperienceTIL.setErrorEnabled(true);
                    dialog_expertExperienceTIL.setError("Experience can not be '0'");
                } else {
                    if (!(checkIsNullOrEmpty(expertExperience))) {
                        dialog_expertExperienceTIL.setErrorEnabled(false);
                    }
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
        dialog_addExpertBN.setOnClickListener(this);
    }

    private void bindDialogControls(View dialogView) {
        dialog_expertNameTIL = dialogView.findViewById(R.id.dialog_subServiceNameTIL);
        dialog_expertExpertiseTIL = dialogView.findViewById(R.id.dialog_subServicePriceTIL);
        dialog_expertExperienceTIL = dialogView.findViewById(R.id.dialog_subServiceDescriptionTIL);
        dialog_expertNameET = dialogView.findViewById(R.id.dialog_subServiceNameET);
        dialog_expertExpertiseET = dialogView.findViewById(R.id.dialog_subServicePriceET);
        dialog_expertExperienceET = dialogView.findViewById(R.id.dialog_subServiceDescriptionET);
        dialog_closeIB = dialogView.findViewById(R.id.dialog_closeIB);
        dialog_addExpertBN = dialogView.findViewById(R.id.dialog_addExpertBN);
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

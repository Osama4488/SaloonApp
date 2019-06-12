package com.example.saloonapp.Fragments.Parlour;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.example.saloonapp.Adapters.Parlour.ServicesRecyclerViewAdapter;
import com.example.saloonapp.Models.ServicesModel;
import com.example.saloonapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServicesFragment extends Fragment implements View.OnClickListener {

    public FloatingActionButton addServiceFAB;
    private List<ServicesModel> servicesModelList;
    public RecyclerView serviceRV;
    private AppCompatTextView titleTV;
    private String TAG = "SERVICES_FRAGMENT";

    // Dialog Controls
    private AlertDialog addServiceDialog;
    private AppCompatButton dialog_addServiceBN;
    private TextInputLayout dialog_serviceNameTIL;
    private AppCompatEditText dialog_serviceNameET;
    private AppCompatImageButton dialog_closeIB;

    //Api Strings
    private String url;
    private MediaType JSON;
    private OkHttpClient client;
    private Request request;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_services, container, false);

        bindControls(view);
        bindListeners();
        hitApiGetAllServices();

        return view;
    }

    private void bindListeners() {
        addServiceFAB.setOnClickListener(this);
    }

    private void hitApiGetAllServices() {
        url = getString(R.string.url) + "services";

        JSON = MediaType.parse("application/json; charset=utf-8");

        client = new OkHttpClient.Builder()
                .build();

        RequestBody body = RequestBody.create( JSON, "" );
        request = new Request.Builder()
                .url( url )
                .header("Authorization", "Bearer " + getToken())
                .get()
                .build();
        client.newCall( request ).enqueue( new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),"Network Error", Toast.LENGTH_LONG).show();
                    }
                });
                Log.e(TAG, "hitApiGetAllServices: onFailure:" + e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.code() == 200){
                    castServicesToList(response);
                } else {
                    Log.e(TAG, "hitApiGetAllServices: onResponse: " + response.code());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Network error, try again later.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        } );
    }

    private void castServicesToList(Response response) {
        try {
            servicesModelList = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(response.body().string());
            for (int i = 0; i < jsonArray.length(); i++) {
                servicesModelList.add(new ServicesModel(
                        jsonArray.getJSONObject(i).getString("Id"),
                        jsonArray.getJSONObject(i).getString("Name")

                ));
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setUpList();
                }
            });
        } catch (Exception e) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "Network error, try again later.", Toast.LENGTH_LONG).show();
                }
            });
            Log.e(TAG, "castservicesToList: " + e);
        }
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

    private String getToken(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("access_token", null);
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
            hitApiAddServices(serviceName);
        }
    }

    private void hitApiAddServices(String serviceName) {
        url = getString(R.string.url) + "services";

        JSON = MediaType.parse("application/json; charset=utf-8");

        client = new OkHttpClient.Builder()
                .build();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Name", serviceName);
        } catch (Exception e) {
            Log.e(TAG, "hitApiAddServices: " + e);
        }

        RequestBody body = RequestBody.create( JSON, jsonObject.toString() );
        request = new Request.Builder()
                .url( url )
                .header("Authorization", "Bearer " + getToken())
                .post(body)
                .build();
        client.newCall( request ).enqueue( new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),"Network Error", Toast.LENGTH_LONG).show();
                    }
                });
                Log.e(TAG, "hitApiAddServices: onFailure:" + e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()){
                    addServiceToList(response);
                } else {
                    Log.e(TAG, "hitApiAddServices: onResponse: " + response.code());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Network error, try again later.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        } );
    }

    private void addServiceToList(Response response) {
        try {
            JSONObject jsonObject = new JSONObject(response.body().string());
            servicesModelList.add(new ServicesModel(
                    jsonObject.getString("Id"),
                    jsonObject.getString("Name")
            ));
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setUpList();
                    addServiceDialog.dismiss();
                }
            });
        } catch (Exception e) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "Network error, try again later.", Toast.LENGTH_LONG).show();
                }
            });
            Log.e(TAG, "addServiceToList: " + e);
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

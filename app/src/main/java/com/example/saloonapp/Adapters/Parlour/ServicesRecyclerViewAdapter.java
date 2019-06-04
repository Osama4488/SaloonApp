package com.example.saloonapp.Adapters.Parlour;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
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

import com.example.saloonapp.Activities.Parlour.SubServiceActivity;
import com.example.saloonapp.Models.ServicesModel;
import com.example.saloonapp.R;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServicesRecyclerViewAdapter extends RecyclerView.Adapter<ServicesRecyclerViewAdapter.ItemServiceViewHolder> implements View.OnClickListener {

    private Activity activity;
    private List<ServicesModel> servicesModelList;
    public RecyclerView serviceRV;
    private AppCompatTextView titleTV;

    // Dialog Controls
    private AlertDialog updateServiceDialog;
    private AppCompatTextView dialog_serviceTitleTV;
    private AppCompatButton dialog_updateServiceBN;
    private TextInputLayout dialog_serviceNameTIL;
    private AppCompatEditText dialog_serviceNameET;
    private AppCompatImageButton dialog_closeIB;
    private int servicePosition;

    //Api Strings
    private String url, TAG = "SERVICE_RV_ADAPTER";
    private MediaType JSON;
    private OkHttpClient client;
    private Request request;

    public ServicesRecyclerViewAdapter(Activity activity, List<ServicesModel> servicesModelList, RecyclerView serviceRV, AppCompatTextView titleTV) {
        this.activity = activity;
        this.servicesModelList = servicesModelList;
        this.serviceRV = serviceRV;
        this.titleTV = titleTV;
    }

    @NonNull
    @Override
    public ItemServiceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_services, viewGroup, false);
        return new ItemServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemServiceViewHolder itemServiceViewHolder, final int i) {
        final ServicesModel item = servicesModelList.get(i);
        if (item != null) {
            itemServiceViewHolder.serviceNameTV.setText(item.getServiceName());

            itemServiceViewHolder.serviceDeleteIB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDialog(item.getServiceName(), i);
                }
            });

            itemServiceViewHolder.serviceCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, SubServiceActivity.class);
                    intent.putExtra("serviceName", item.getServiceName());
                    intent.putExtra("serviceId", item.getServiceId());
                    activity.startActivity(intent);
                }
            });

            itemServiceViewHolder.serviceCV.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showUpdateServiceDialog(i);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return servicesModelList.size();
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

    private String getToken(){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("access_token", null);
    }

    private void hitApiDeleteService(final int position) {
        url = activity.getString(R.string.url) + "services/" + servicesModelList.get(position).getServiceId();

        client = new OkHttpClient.Builder()
                .build();

        request = new Request.Builder()
                .url( url )
                .header("Authorization", "Bearer " + getToken())
                .delete()
                .build();
        client.newCall( request ).enqueue( new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity,"Network Error", Toast.LENGTH_LONG).show();
                    }
                });
                Log.e(TAG, "hitApiDeleteService: " + e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()){
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, servicesModelList.get(position).getServiceName() + " service removed", Toast.LENGTH_SHORT).show();
                            servicesModelList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, servicesModelList.size());
                            if (servicesModelList.size() == 0) {
                                controlsVisibility(View.VISIBLE);
                            } else {
                                controlsVisibility(View.GONE);
                            }
                        }
                    });
                } else {
                    Log.e(TAG, "hitApiDeleteService: onResponse: " + response.code());
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "Network error, try again later.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        } );
    }

    private void hitApiUpdateService(final int position) {
        url = activity.getString(R.string.url) + "services/" + servicesModelList.get(position).getServiceId();

        client = new OkHttpClient.Builder()
                .build();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Id", servicesModelList.get(position).getServiceId());
            jsonObject.put("Name", dialog_serviceNameET.getText().toString());
        } catch (Exception e) {
            Log.e(TAG, "hitApiUpdateService: " + e);
        }

        JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create( JSON, jsonObject.toString());

        request = new Request.Builder()
                .url( url )
                .header("Authorization", "Bearer " + getToken())
                .put(body)
                .build();
        client.newCall( request ).enqueue( new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity,"Network Error", Toast.LENGTH_LONG).show();
                    }
                });
                Log.e(TAG, "hitApiUpdateService: onFailure: " + e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()){
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            servicesModelList.set(position, new ServicesModel(
                                    servicesModelList.get(position).getServiceId(),
                                    dialog_serviceNameET.getText().toString()
                            ));
                            notifyItemChanged(position);
                            notifyItemRangeChanged(position, servicesModelList.size());
                            updateServiceDialog.dismiss();
                        }
                    });
                } else {
                    Log.e(TAG, "hitApiUpdateService: onResponse: " + response.code());
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "Network error, try again later.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        } );
    }

    private void updateService(int servicePosition) {
        String serviceName = dialog_serviceNameET.getText().toString();
        if (checkIsNullOrEmpty(serviceName)) {
            dialog_serviceNameTIL.setErrorEnabled(true);
            dialog_serviceNameTIL.setError("Can not be empty");
            animateDialog(updateServiceDialog);
        } else if (!(dialog_serviceNameTIL.isErrorEnabled())) {
            hitApiUpdateService(servicePosition);
        }
    }

    private void animateDialog(AlertDialog dialog) {
        dialog.getWindow()
                .getDecorView()
                .animate()
                .translationX(16f)
                .setInterpolator(new CycleInterpolator(7f));
    }

    private void showUpdateServiceDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_service, null);
        builder.setView(dialogView);

        bindDialogControls(dialogView, position);
        bindDialogListeners();
        setDialogEdittextError();

        updateServiceDialog = builder.create();
        updateServiceDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        updateServiceDialog.setCanceledOnTouchOutside(true);
        updateServiceDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        updateServiceDialog.show();
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
        dialog_updateServiceBN.setOnClickListener(this);
    }

    private void bindDialogControls(View dialogView, int position) {
        dialog_serviceTitleTV = dialogView.findViewById(R.id.dialog_serviceTitleTV);
        dialog_serviceNameTIL = dialogView.findViewById(R.id.dialog_serviceNameTIL);
        dialog_serviceNameET = dialogView.findViewById(R.id.dialog_serviceNameET);
        dialog_closeIB = dialogView.findViewById(R.id.dialog_closeIB);
        dialog_updateServiceBN = dialogView.findViewById(R.id.dialog_addServiceBN);

        dialog_serviceNameET.setHint(servicesModelList.get(position).getServiceName());

        dialog_serviceNameTIL.setHint("");

        dialog_serviceTitleTV.setText("Update Service");
        dialog_updateServiceBN.setText("Update");
        servicePosition = position;
    }

    public void deleteDialog(final String serviceName, final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle("Remove Service?");
        alertDialogBuilder.setMessage("Are you sure, You want to remove " + serviceName.toUpperCase() + " service?");
        alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hitApiDeleteService(position);
                dialog.dismiss();
            }
        });

        alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {
        if (v == dialog_closeIB) {
            updateServiceDialog.dismiss();
        } else if (v == dialog_updateServiceBN) {
            updateService(servicePosition);
        }
    }

    public class ItemServiceViewHolder extends RecyclerView.ViewHolder {

        private MaterialCardView serviceCV;
        private AppCompatTextView serviceNameTV;
        private AppCompatImageButton serviceDeleteIB;

        public ItemServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceCV = itemView.findViewById(R.id.item_serviceCV);
            serviceNameTV = itemView.findViewById(R.id.item_serviceName);
            serviceDeleteIB = itemView.findViewById(R.id.item_deleteIB);
        }
    }

}

package com.example.saloonapp.Adapters.Parlour;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
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

import com.example.saloonapp.Models.ServicesModel;
import com.example.saloonapp.Models.SubServicesModel;
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

public class SubServicesRecyclerViewAdapter extends RecyclerView.Adapter<SubServicesRecyclerViewAdapter.ItemSubServiceViewHolder> implements View.OnClickListener {

    private Activity activity;
    private String title;
    private List<SubServicesModel> subServicesModelList;
    private RecyclerView subServicesRV;
    private AppCompatTextView titleTV;

    // Dialog Controls
    private AlertDialog updateSubServiceDialog;
    private AppCompatTextView dialog_subServiceTitleTV;
    private AppCompatButton dialog_addSubServiceBN;
    private TextInputLayout dialog_subServiceNameTIL, dialog_subServicePriceTIL, dialog_subServiceDescriptionTIL;
    private AppCompatEditText dialog_subServiceNameET, dialog_subServicePriceET, dialog_subServiceDescriptionET;
    private AppCompatImageButton dialog_closeIB;
    private int subServicePosition;

    //Api Strings
    private String url, TAG = "SUB_SERVICE_RV_ADAPTER";
    private MediaType JSON;
    private OkHttpClient client;
    private Request request;

    public SubServicesRecyclerViewAdapter(Activity activity, String title, List<SubServicesModel> subServicesModelList, RecyclerView subServicesRV, AppCompatTextView titleTV) {
        this.activity = activity;
        this.title = title;
        this.subServicesModelList = subServicesModelList;
        this.subServicesRV = subServicesRV;
        this.titleTV = titleTV;
    }

    @NonNull
    @Override
    public ItemSubServiceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_sub_services, viewGroup, false);
        return new ItemSubServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemSubServiceViewHolder itemSubServiceViewHolder, final int i) {
        final SubServicesModel item = subServicesModelList.get(i);
        if (item != null) {
            itemSubServiceViewHolder.subServiceNameTV.setText(item.getSubServiceName());
            itemSubServiceViewHolder.subServicePriceTV.setText("Rs. " + item.getSubServicePrice());
            itemSubServiceViewHolder.subServiceDescriptionTV.setText(item.getSubServiceDescription());

            itemSubServiceViewHolder.expertDeleteIB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDialog(item.getSubServiceName(), i);
                }
            });

            itemSubServiceViewHolder.subServiceCV.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showUpdateSubServiceDialog(i);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return subServicesModelList.size();
    }

    private String getToken(){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("access_token", null);
    }

    private void hitApiDeleteSubService(final int position) {
        url = activity.getString(R.string.url) + "subservices/" + subServicesModelList.get(position).getSubServiceId();

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
                Log.e(TAG, "hitApiDeleteSubService: onFailure: " + e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()){
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, subServicesModelList.get(position).getSubServiceName() + " removed", Toast.LENGTH_SHORT).show();
                            subServicesModelList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, subServicesModelList.size());
                            if (subServicesModelList.size() == 0) {
                                controlsVisibility(View.VISIBLE);
                            } else {
                                controlsVisibility(View.GONE);
                            }
                        }
                    });
                } else {
                    Log.e(TAG, "hitApiDeleteSubService: onResponse: " + response.code());
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

    private void hitApiUpdateSubService(final int position) {
        url = activity.getString(R.string.url) + "subservices/" + subServicesModelList.get(position).getSubServiceId();

        client = new OkHttpClient.Builder()
                .build();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Id", subServicesModelList.get(position).getSubServiceId());
            jsonObject.put("Name", dialog_subServiceNameET.getText().toString());
            jsonObject.put("Price", dialog_subServicePriceET.getText().toString());
            jsonObject.put("Description", dialog_subServiceDescriptionET.getText().toString());
            jsonObject.put("ServiceId", subServicesModelList.get(position).getServiceId());
        } catch (Exception e) {
            Log.e(TAG, "hitApiUpdateSubService: " + e);
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
                Log.e(TAG, "hitApiUpdateSubService: onFailure: " + e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()){
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            subServicesModelList.set(position, new SubServicesModel(
                                    subServicesModelList.get(position).getSubServiceId(),
                                    dialog_subServiceNameET.getText().toString(),
                                    dialog_subServiceDescriptionET.getText().toString(),
                                    subServicesModelList.get(position).getServiceId(),
                                    Integer.valueOf(dialog_subServicePriceET.getText().toString())
                            ));
                            notifyItemChanged(position);
                            notifyItemRangeChanged(position, subServicesModelList.size());
                            updateSubServiceDialog.dismiss();
                        }
                    });
                } else {
                    Log.e(TAG, "hitApiUpdateSubService: onResponse: " + response.code());
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

    private void controlsVisibility(int visibliltiy) {
        if (View.VISIBLE == visibliltiy) {
            titleTV.setVisibility(View.VISIBLE);
            titleTV.setText("'+' to add " + title + " service");
            subServicesRV.setVisibility(View.GONE);
        } else {
            titleTV.setVisibility(View.GONE);
            subServicesRV.setVisibility(View.VISIBLE);
        }
    }

    private void updateSubService(int servicePosition) {
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
            animateDialog(updateSubServiceDialog);
        } else if (!(dialog_subServiceNameTIL.isErrorEnabled() && dialog_subServicePriceTIL.isErrorEnabled() && dialog_subServiceDescriptionTIL.isErrorEnabled())) {
            hitApiUpdateSubService(servicePosition);
        }
    }

    private void animateDialog(AlertDialog dialog) {
        dialog.getWindow()
                .getDecorView()
                .animate()
                .translationX(16f)
                .setInterpolator(new CycleInterpolator(7f));
    }

    private void showUpdateSubServiceDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_sub_service, null);
        builder.setView(dialogView);

        bindDialogControls(dialogView, position);
        bindDialogListeners();
        setDialogEdittextError();

        updateSubServiceDialog = builder.create();
        updateSubServiceDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        updateSubServiceDialog.setCanceledOnTouchOutside(true);
        updateSubServiceDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        updateSubServiceDialog.show();
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

    private void bindDialogControls(View dialogView, int position) {
        dialog_subServiceTitleTV = dialogView.findViewById(R.id.dialog_subServiceTitleTV);
        dialog_subServiceNameTIL = dialogView.findViewById(R.id.dialog_subServiceNameTIL);
        dialog_subServicePriceTIL = dialogView.findViewById(R.id.dialog_subServicePriceTIL);
        dialog_subServiceDescriptionTIL = dialogView.findViewById(R.id.dialog_subServiceDescriptionTIL);
        dialog_subServiceNameET = dialogView.findViewById(R.id.dialog_subServiceNameET);
        dialog_subServicePriceET = dialogView.findViewById(R.id.dialog_subServicePriceET);
        dialog_subServiceDescriptionET = dialogView.findViewById(R.id.dialog_subServiceDescriptionET);
        dialog_closeIB = dialogView.findViewById(R.id.dialog_closeIB);
        dialog_addSubServiceBN = dialogView.findViewById(R.id.dialog_addSubServiceBN);

        dialog_subServiceNameET.setHint(subServicesModelList.get(position).getSubServiceName());
        dialog_subServicePriceET.setHint(subServicesModelList.get(position).getSubServicePrice().toString());
        dialog_subServiceDescriptionET.setHint(subServicesModelList.get(position).getSubServiceDescription());

        dialog_subServiceNameTIL.setHint("");
        dialog_subServicePriceTIL.setHint("");
        dialog_subServiceDescriptionTIL.setHint("");

        dialog_subServiceTitleTV.setText("Update " + title + " Expert");
        dialog_addSubServiceBN.setText("Update");
        subServicePosition = position;
    }

    public void deleteDialog(final String subServiceName, final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle("Remove Service?");
        alertDialogBuilder.setMessage("Are you sure, You want to remove " + subServiceName.toUpperCase() + " service?");
        alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hitApiDeleteSubService(position);
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
            updateSubServiceDialog.dismiss();
        } else if (v == dialog_addSubServiceBN) {
            updateSubService(subServicePosition);
        }
    }

    public class ItemSubServiceViewHolder extends RecyclerView.ViewHolder {

        private MaterialCardView subServiceCV;
        private AppCompatTextView subServiceNameTV, subServicePriceTV, subServiceDescriptionTV;
        private AppCompatImageButton expertDeleteIB;

        public ItemSubServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            subServiceCV = itemView.findViewById(R.id.item_subServicesCV);
            subServiceNameTV = itemView.findViewById(R.id.item_subServiceName);
            subServicePriceTV = itemView.findViewById(R.id.item_expertExpertise);
            subServiceDescriptionTV = itemView.findViewById(R.id.item_subServiceDescrption);
            expertDeleteIB = itemView.findViewById(R.id.item_deleteIB);
        }
    }

}



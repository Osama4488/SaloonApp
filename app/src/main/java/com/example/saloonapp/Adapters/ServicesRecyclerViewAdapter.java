package com.example.saloonapp.Adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.CycleInterpolator;
import android.widget.Toast;

import com.example.saloonapp.Activities.SubServiceActivity;
import com.example.saloonapp.Models.ServicesModel;
import com.example.saloonapp.R;

import java.util.List;

public class ServicesRecyclerViewAdapter extends RecyclerView.Adapter<ServicesRecyclerViewAdapter.ItemServiceViewHolder> implements View.OnClickListener {

    private Activity activity;
    private List<ServicesModel> servicesModelList;

    // Dialog Controls
    private AlertDialog updateServiceDialog;
    private AppCompatTextView dialog_serviceTitleTV;
    private AppCompatButton dialog_updateServiceBN;
    private TextInputLayout dialog_serviceNameTIL;
    private AppCompatEditText dialog_serviceNameET;
    private AppCompatImageButton dialog_closeIB;
    private int servicePosition;

    public ServicesRecyclerViewAdapter(Activity activity, List<ServicesModel> servicesModelList) {
        this.activity = activity;
        this.servicesModelList = servicesModelList;
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

    private void deleteServiceAtPosition(int position) {
        servicesModelList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, servicesModelList.size());
    }

    private void updateServiceAtPosition(int position) {
        servicesModelList.set(position, new ServicesModel(
                servicesModelList.get(position).getServiceId(),
                dialog_serviceNameET.getText().toString()
        ));
        notifyItemChanged(position);
        notifyItemRangeChanged(position, servicesModelList.size());
        updateServiceDialog.dismiss();
    }

    private void updateService(int servicePosition) {
        String serviceName = dialog_serviceNameET.getText().toString();
        if (checkIsNullOrEmpty(serviceName)) {
            dialog_serviceNameTIL.setErrorEnabled(true);
            dialog_serviceNameTIL.setError("Can not be empty");
            animateDialog(updateServiceDialog);
        } else if (!(dialog_serviceNameTIL.isErrorEnabled())) {
            updateServiceAtPosition(servicePosition);
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
                deleteServiceAtPosition(position);
                Toast.makeText(activity, serviceName + " Removed", Toast.LENGTH_SHORT).show();
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

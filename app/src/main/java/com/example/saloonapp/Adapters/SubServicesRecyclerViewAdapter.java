package com.example.saloonapp.Adapters;

import android.app.Activity;
import android.content.DialogInterface;
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

import com.example.saloonapp.Models.SubServicesModel;
import com.example.saloonapp.R;

import java.util.List;

public class SubServicesRecyclerViewAdapter extends RecyclerView.Adapter<SubServicesRecyclerViewAdapter.ItemSubServiceViewHolder> implements View.OnClickListener {

    private Activity activity;
    private String title;
    private List<SubServicesModel> subServicesModelList;

    // Dialog Controls
    private AlertDialog updateSubServiceDialog;
    private AppCompatTextView dialog_subServiceTitleTV;
    private AppCompatButton dialog_addSubServiceBN;
    private TextInputLayout dialog_subServiceNameTIL, dialog_subServicePriceTIL, dialog_subServiceDescriptionTIL;
    private AppCompatEditText dialog_subServiceNameET, dialog_subServicePriceET, dialog_subServiceDescriptionET;
    private AppCompatImageButton dialog_closeIB;

    private int subServicePosition;

    public SubServicesRecyclerViewAdapter(Activity activity, String title,  List<SubServicesModel> subServicesModelList) {
        this.activity = activity;
        this.title = title;
        this.subServicesModelList = subServicesModelList;
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

    private void deleteSubServiceAtPosition(int position) {
        subServicesModelList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, subServicesModelList.size());
    }

    private void updateSubServiceAtPosition(int position) {
        subServicesModelList.set(position, new SubServicesModel(
                subServicesModelList.get(position).getSubServiceId(),
                dialog_subServiceNameET.getText().toString(),
                dialog_subServiceDescriptionET.getText().toString(),
                Integer.valueOf(dialog_subServicePriceET.getText().toString())
        ));
        notifyItemChanged(position);
        notifyItemRangeChanged(position, subServicesModelList.size());
        updateSubServiceDialog.dismiss();
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
            updateSubServiceAtPosition(servicePosition);
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
                deleteSubServiceAtPosition(position);
                Toast.makeText(activity, subServiceName + " Removed", Toast.LENGTH_SHORT).show();
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
            subServicePriceTV = itemView.findViewById(R.id.item_subServicePrice);
            subServiceDescriptionTV = itemView.findViewById(R.id.item_subServiceDescrption);
            expertDeleteIB = itemView.findViewById(R.id.item_deleteIB);
        }
    }

}



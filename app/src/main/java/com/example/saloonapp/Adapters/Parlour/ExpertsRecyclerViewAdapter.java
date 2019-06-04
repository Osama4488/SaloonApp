package com.example.saloonapp.Adapters.Parlour;

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

import com.example.saloonapp.Models.ExpertsModel;
import com.example.saloonapp.R;

import java.util.List;

public class ExpertsRecyclerViewAdapter extends RecyclerView.Adapter<ExpertsRecyclerViewAdapter.ItemExpertViewHolder> implements View.OnClickListener {

    private Activity activity;
    private List<ExpertsModel> expertsModelList;

    // Dialog Controls
    private AlertDialog updateExpertDialog;
    private AppCompatTextView dialog_expertTitleTV;
    private AppCompatButton dialog_updateExpertBN;
    private TextInputLayout dialog_expertNameTIL, dialog_expertExpertiseTIL, dialog_expertExperienceTIL;
    private AppCompatEditText dialog_expertNameET, dialog_expertExpertiseET, dialog_expertExperienceET;
    private AppCompatImageButton dialog_closeIB;
    private int expertPosition;

    public ExpertsRecyclerViewAdapter(Activity activity, List<ExpertsModel> expertsModelList, RecyclerView expertsRV, AppCompatTextView titleTV) {
        this.activity = activity;
        this.expertsModelList = expertsModelList;
    }


    @NonNull
    @Override
    public ItemExpertViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_experts, viewGroup, false);
        return new ItemExpertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemExpertViewHolder itemExpertViewHolder, final int i) {
        final ExpertsModel item = expertsModelList.get(i);
        if (item != null) {
            itemExpertViewHolder.expertNameTV.setText(item.getExpertName());
            itemExpertViewHolder.expertExpertiseTV.setText(item.getExpertExpertise());
            itemExpertViewHolder.expertExperienceTV.setText(item.getExpertExperience() + " years of experience");

            itemExpertViewHolder.expertDeleteIB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDialog(item.getExpertName(), i);
                }
            });

            itemExpertViewHolder.expertCV.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showUpdateExpertDialog(i);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return expertsModelList.size();
    }

    private void deleteExpertAtPosition(int position) {
        expertsModelList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, expertsModelList.size());
    }

    private void updateExpertAtPosition(int position) {
        expertsModelList.set(position, new ExpertsModel(
                expertsModelList.get(position).getExpertId(),
                dialog_expertNameET.getText().toString(),
                dialog_expertExpertiseET.getText().toString(),
                dialog_expertExperienceET.getText().toString()
        ));
        notifyItemChanged(position);
        notifyItemRangeChanged(position, expertsModelList.size());
        updateExpertDialog.dismiss();
    }

    private void updateExpert(int servicePosition) {
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
            animateDialog(updateExpertDialog);
        } else if (!(dialog_expertNameTIL.isErrorEnabled() && dialog_expertExpertiseTIL.isErrorEnabled() && dialog_expertExperienceTIL.isErrorEnabled())) {
            updateExpertAtPosition(servicePosition);
        }
    }

    private void animateDialog(AlertDialog dialog) {
        dialog.getWindow()
                .getDecorView()
                .animate()
                .translationX(16f)
                .setInterpolator(new CycleInterpolator(7f));
    }

    private void showUpdateExpertDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_expert, null);
        builder.setView(dialogView);

        bindDialogControls(dialogView, position);
        bindDialogListeners();
        setDialogEdittextError();

        updateExpertDialog = builder.create();
        updateExpertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        updateExpertDialog.setCanceledOnTouchOutside(true);
        updateExpertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        updateExpertDialog.show();
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
        dialog_updateExpertBN.setOnClickListener(this);
    }

    private void bindDialogControls(View dialogView, int position) {
        dialog_expertTitleTV = dialogView.findViewById(R.id.dialog_expertTitleTV);
        dialog_expertNameTIL = dialogView.findViewById(R.id.dialog_subServiceNameTIL);
        dialog_expertExpertiseTIL = dialogView.findViewById(R.id.dialog_subServicePriceTIL);
        dialog_expertExperienceTIL = dialogView.findViewById(R.id.dialog_subServiceDescriptionTIL);
        dialog_expertNameET = dialogView.findViewById(R.id.dialog_subServiceNameET);
        dialog_expertExpertiseET = dialogView.findViewById(R.id.dialog_subServicePriceET);
        dialog_expertExperienceET = dialogView.findViewById(R.id.dialog_subServiceDescriptionET);
        dialog_closeIB = dialogView.findViewById(R.id.dialog_closeIB);
        dialog_updateExpertBN = dialogView.findViewById(R.id.dialog_addExpertBN);

        dialog_expertNameET.setHint(expertsModelList.get(position).getExpertName());
        dialog_expertExpertiseET.setHint(expertsModelList.get(position).getExpertExpertise());
        dialog_expertExperienceET.setHint(expertsModelList.get(position).getExpertExperience());

        dialog_expertNameTIL.setHint("");
        dialog_expertExpertiseTIL.setHint("");
        dialog_expertExperienceTIL.setHint("");

        dialog_expertTitleTV.setText("Update Expert");
        dialog_updateExpertBN.setText("Update");
        expertPosition = position;
    }

    public void deleteDialog(final String expertName, final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle("Remove Service?");
        alertDialogBuilder.setMessage("Are you sure, You want to remove " + expertName.toUpperCase() + " service?");
        alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteExpertAtPosition(position);
                Toast.makeText(activity, expertName + " Removed", Toast.LENGTH_SHORT).show();
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
            updateExpertDialog.dismiss();
        } else if (v == dialog_updateExpertBN) {
            updateExpert(expertPosition);
        }
    }

    public class ItemExpertViewHolder extends RecyclerView.ViewHolder {

        private MaterialCardView expertCV;
        private AppCompatTextView expertNameTV, expertExpertiseTV, expertExperienceTV;
        private AppCompatImageButton expertDeleteIB;

        public ItemExpertViewHolder(@NonNull View itemView) {
            super(itemView);
            expertCV = itemView.findViewById(R.id.item_expertCV);
            expertNameTV = itemView.findViewById(R.id.item_expertName);
            expertExpertiseTV = itemView.findViewById(R.id.item_expertExpertise);
            expertExperienceTV = itemView.findViewById(R.id.item_expertExperience);
            expertDeleteIB = itemView.findViewById(R.id.item_deleteIB);
        }
    }

}


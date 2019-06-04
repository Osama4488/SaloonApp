package com.example.saloonapp.Adapters.User;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.saloonapp.Models.ExpertsModel;
import com.example.saloonapp.Models.SubServicesModel;
import com.example.saloonapp.R;

import java.util.List;

public class ParlourExpertsRecyclerViewAdapter extends RecyclerView.Adapter<ParlourExpertsRecyclerViewAdapter.ItemParlourExpertViewHolder>{

    private Activity activity;
    private List<ExpertsModel> expertsModelList;

    public ParlourExpertsRecyclerViewAdapter(Activity activity, List<ExpertsModel> expertsModelList) {
        this.activity = activity;
        this.expertsModelList = expertsModelList;
    }


    @NonNull
    @Override
    public ItemParlourExpertViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_parlour_expert, viewGroup, false);
        return new ItemParlourExpertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemParlourExpertViewHolder itemParlourExpertViewHolder, int position) {
        ExpertsModel item = expertsModelList.get(position);
        if (item != null) {
            itemParlourExpertViewHolder.expertNameTV.setText(item.getExpertName());
            itemParlourExpertViewHolder.expertExpertiseTV.setText(item.getExpertExpertise());
            itemParlourExpertViewHolder.expertExperienceTV.setText(item.getExpertExperience() + " years of experience");
        }
    }

    @Override
    public int getItemCount() {
        return expertsModelList.size();
    }

    public class ItemParlourExpertViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView expertNameTV, expertExpertiseTV, expertExperienceTV;

        public ItemParlourExpertViewHolder(@NonNull View itemView) {
            super(itemView);

            expertNameTV = itemView.findViewById(R.id.item_expertName);
            expertExpertiseTV = itemView.findViewById(R.id.item_expertExpertise);
            expertExperienceTV = itemView.findViewById(R.id.item_expertExperience);
        }
    }
}


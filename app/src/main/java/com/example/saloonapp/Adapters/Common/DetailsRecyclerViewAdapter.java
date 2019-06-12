package com.example.saloonapp.Adapters.Common;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.saloonapp.Models.DetailsModel;
import com.example.saloonapp.R;

import java.util.List;

public class DetailsRecyclerViewAdapter extends RecyclerView.Adapter<DetailsRecyclerViewAdapter.ItemDetailViewHolder> {

    private Activity activity;
    private List<DetailsModel> detailsModelList;

    public DetailsRecyclerViewAdapter(Activity activity, List<DetailsModel> detailsModelList) {
        this.activity = activity;
        this.detailsModelList = detailsModelList;
    }

    @NonNull
    @Override
    public ItemDetailViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_user_status, viewGroup, false);
        return new ItemDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemDetailViewHolder itemDetailViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return detailsModelList.size();
    }

    public class ItemDetailViewHolder extends RecyclerView.ViewHolder {
        public ItemDetailViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}

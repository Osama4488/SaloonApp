package com.example.saloonapp.Adapters.User;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.saloonapp.Activities.User.ParlourDetailActivity;
import com.example.saloonapp.Models.ParlourModel;
import com.example.saloonapp.R;

import java.util.List;

public class ParlourRecyclerViewAdapter extends RecyclerView.Adapter<ParlourRecyclerViewAdapter.ItemParlourCardViewHolder> {

    private Activity activity;
    private List<ParlourModel> parlourModelList;
    private int layout;

    public ParlourRecyclerViewAdapter(Activity activity, List<ParlourModel> parlourModelList, int layout) {
        this.activity = activity;
        this.parlourModelList = parlourModelList;
        this.layout = layout;
    }

    @NonNull
    @Override
    public ItemParlourCardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(layout, viewGroup, false);
        return new ParlourRecyclerViewAdapter.ItemParlourCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemParlourCardViewHolder itemParlourCardViewHolder, int position) {
        final ParlourModel item = parlourModelList.get(position);
        if (item != null) {
            itemParlourCardViewHolder.parlourNameTV.setText(item.getParlourName());
            itemParlourCardViewHolder.parlourRatingTV.setText("Rating: " + item.getParlourRating());
            itemParlourCardViewHolder.parlourNumberTV.setText(item.getParlourNumber());
            itemParlourCardViewHolder.parlourCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, ParlourDetailActivity.class);
                    intent.putExtra("parlourId", item.getParlourId());
                    activity.startActivity(intent);

                }
            });
        }
    }

    public void filterList(List<ParlourModel> filteredList){
        parlourModelList = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return parlourModelList.size();
    }

    public class ItemParlourCardViewHolder extends RecyclerView.ViewHolder {

        private MaterialCardView parlourCV;
        private AppCompatTextView parlourNameTV, parlourRatingTV, parlourNumberTV;

        public ItemParlourCardViewHolder(@NonNull View itemView) {
            super(itemView);
            parlourCV = itemView.findViewById(R.id.item_parlourCV);
            parlourNameTV = itemView.findViewById(R.id.item_parlourNameTV);
            parlourNumberTV = itemView.findViewById(R.id.item_parlourNumberTV);
            parlourRatingTV = itemView.findViewById(R.id.item_parlourRatingTV);
        }
    }
}

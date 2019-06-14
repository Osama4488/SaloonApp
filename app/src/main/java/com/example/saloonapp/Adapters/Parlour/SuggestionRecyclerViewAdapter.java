package com.example.saloonapp.Adapters.Parlour;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.saloonapp.Activities.User.ParlourDetailActivity;
import com.example.saloonapp.Models.SuggestionModel;
import com.example.saloonapp.R;

import java.util.List;

public class SuggestionRecyclerViewAdapter extends RecyclerView.Adapter<SuggestionRecyclerViewAdapter.ItemSuggestionViewHolder> {

    private Activity activity;
    private List<SuggestionModel> suggestionModelList;

    public SuggestionRecyclerViewAdapter(Activity activity, List<SuggestionModel> suggestionModelList) {
        this.activity = activity;
        this.suggestionModelList = suggestionModelList;
    }

    @NonNull
    @Override
    public ItemSuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_suggestion, viewGroup, false);
        return new ItemSuggestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemSuggestionViewHolder itemSuggestionViewHolder, int position) {
        final SuggestionModel item = suggestionModelList.get(position);
        if (item != null) {
            itemSuggestionViewHolder.parlourNameTV.setText(item.getParlourName());
            itemSuggestionViewHolder.parlourBookingTV.setText("Bookings Completed: " + item.getParlourBookings());
            itemSuggestionViewHolder.parlourExpertsTV.setText("Available Experts: " + item.getParlourExpert());
            itemSuggestionViewHolder.parlourRB.setRating(Float.valueOf(item.getParlourRating()));
            itemSuggestionViewHolder.parlourRL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, ParlourDetailActivity.class);
                    intent.putExtra("parlourId", item.getParlourId());
                    activity.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return suggestionModelList.size();
    }

    public class ItemSuggestionViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout parlourRL;
        private AppCompatTextView parlourNameTV, parlourBookingTV, parlourExpertsTV;
        private AppCompatRatingBar parlourRB;

        public ItemSuggestionViewHolder(@NonNull View itemView) {
            super(itemView);

            parlourRL = itemView.findViewById(R.id.item_parlourRL);
            parlourNameTV = itemView.findViewById(R.id.item_parlourNameTV);
            parlourBookingTV = itemView.findViewById(R.id.item_parlourBookingsTV);
            parlourExpertsTV = itemView.findViewById(R.id.item_palourExpertsTV);
            parlourRB = itemView.findViewById(R.id.item_parlourRB);
        }
    }
}

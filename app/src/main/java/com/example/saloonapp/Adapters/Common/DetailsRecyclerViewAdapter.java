package com.example.saloonapp.Adapters.Common;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
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
        View view = LayoutInflater.from(activity).inflate(R.layout.item_details, viewGroup, false);
        return new ItemDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemDetailViewHolder itemDetailViewHolder, int position) {
        DetailsModel item = detailsModelList.get(position);
        if (item != null) {
            itemDetailViewHolder.subServiceNameTV.setText(item.getSubServieName());

            Double valueToDouble = Double.parseDouble(item.getSubServicePrice());
            Integer price = valueToDouble.intValue();
            Integer qty = Integer.valueOf(item.getSubServiceQty());

            itemDetailViewHolder.subServicePriceTV.setText(" = " + price);
            itemDetailViewHolder.subServiceQtyTV.setText(" x " + qty);
            itemDetailViewHolder.subServiceTotalTV.setText("RS. " + price * qty);
        }
    }

    @Override
    public int getItemCount() {
        return detailsModelList.size();
    }

    public class ItemDetailViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView subServiceNameTV, subServicePriceTV, subServiceQtyTV, subServiceTotalTV;

        public ItemDetailViewHolder(@NonNull View itemView) {
            super(itemView);

            subServiceNameTV = itemView.findViewById(R.id.item_subServiceNameTV);
            subServicePriceTV = itemView.findViewById(R.id.item_subServicePriceTV);
            subServiceQtyTV = itemView.findViewById(R.id.item_qtyTV);
            subServiceTotalTV = itemView.findViewById(R.id.item_totalTV);
        }
    }
}

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

import com.example.saloonapp.Models.SubServicesModel;
import com.example.saloonapp.R;

import java.util.List;

public class ParlourSubServiceRecyclerViewAdapter extends RecyclerView.Adapter<ParlourSubServiceRecyclerViewAdapter.ItemParlourSubServiceViewHolder>{

    private Activity activity;
    private List<SubServicesModel> subServicesModelList;

    public ParlourSubServiceRecyclerViewAdapter(Activity activity, List<SubServicesModel> subServicesModelList) {
        this.activity = activity;
        this.subServicesModelList = subServicesModelList;
    }

    @NonNull
    @Override
    public ItemParlourSubServiceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_parlour_sub_service, viewGroup, false);
        return new ItemParlourSubServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemParlourSubServiceViewHolder itemParlourSubServiceViewHolder, int position) {
        SubServicesModel item = subServicesModelList.get(position);
        if (item != null) {
            itemParlourSubServiceViewHolder.subServiceName.setText(item.getSubServiceName());
            itemParlourSubServiceViewHolder.subServicePrice.setText("Rs: " + item.getSubServicePrice());
            itemParlourSubServiceViewHolder.subServiceDescription.setText(item.getSubServiceDescription());
            itemParlourSubServiceViewHolder.bookBN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(activity, "Booked.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return subServicesModelList.size();
    }

    public class ItemParlourSubServiceViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView subServiceName, subServicePrice, subServiceDescription;
        private AppCompatButton bookBN;

        public ItemParlourSubServiceViewHolder(@NonNull View itemView) {
            super(itemView);

            subServiceName = itemView.findViewById(R.id.item_subServiceName);
            subServicePrice = itemView.findViewById(R.id.item_expertExpertise);
            subServiceDescription = itemView.findViewById(R.id.item_subServiceDescrption);
            bookBN = itemView.findViewById(R.id.item_bookBN);
        }
    }
}

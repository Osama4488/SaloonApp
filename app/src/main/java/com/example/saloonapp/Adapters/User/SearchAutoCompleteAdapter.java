package com.example.saloonapp.Adapters.User;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;

import com.example.saloonapp.Models.ParlourModel;
import com.example.saloonapp.R;

import java.util.ArrayList;
import java.util.List;

public class SearchAutoCompleteAdapter extends ArrayAdapter<ParlourModel> {

    private Activity activity;
    private int resource;
    private List<ParlourModel> parlourModelList;

    public SearchAutoCompleteAdapter(Activity activity , int resource, List<ParlourModel> parlourModelList) {
        super(activity, resource, parlourModelList);
        this.activity = activity;
        this.resource = resource;
        this.parlourModelList = parlourModelList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(resource, parent, false);
        }
        ParlourModel item = parlourModelList.get(position);
        if (item != null) {
            AppCompatTextView parlourName = convertView.findViewById(R.id.item_parlourNameTV);
            parlourName.setText(item.getParlourName());
        }
        return convertView;
    }

    @Override
    public ParlourModel getItem(int position) {
        return parlourModelList.get(position);
    }

    @Override
    public int getCount() {
        return parlourModelList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return itemFilter;
    }

    private Filter itemFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<ParlourModel> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(parlourModelList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (ParlourModel item : parlourModelList) {
                    if (item.getParlourName().toLowerCase().contains(filterPattern)) {
                        suggestions.add(item);
                    }
//                    else if (item.getProductBrand().toLowerCase().contains(filterPattern)){
//                        suggestions.add(item);
//                    }
                }
            }

            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((ParlourModel) resultValue).getParlourName();
        }
    };

}



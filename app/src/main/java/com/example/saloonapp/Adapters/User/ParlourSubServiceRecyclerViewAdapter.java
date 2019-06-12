package com.example.saloonapp.Adapters.User;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.saloonapp.Activities.Common.LoginActivity;
import com.example.saloonapp.Models.SubServicesModel;
import com.example.saloonapp.R;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ParlourSubServiceRecyclerViewAdapter extends RecyclerView.Adapter<ParlourSubServiceRecyclerViewAdapter.ItemParlourSubServiceViewHolder>{

    private Activity activity;
    private List<SubServicesModel> subServicesModelList;

    //API strings
    private String url, TAG = "PARLOUR_SUB_SERVICE_RVA";
    private MediaType JSON;
    private OkHttpClient client;
    private Request request;

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
        final SubServicesModel item = subServicesModelList.get(position);
        if (item != null) {
            itemParlourSubServiceViewHolder.subServiceName.setText(item.getSubServiceName());
            itemParlourSubServiceViewHolder.subServicePrice.setText("Rs: " + item.getSubServicePrice());
            itemParlourSubServiceViewHolder.subServiceDescription.setText(item.getSubServiceDescription());
            itemParlourSubServiceViewHolder.bookBN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hitApiAddToCart(item);
                }
            });
        }
    }

    private void hitApiAddToCart(SubServicesModel item) {
        url = activity.getString(R.string.url) + "carts";

        JSON = MediaType.parse("application/json; charset=utf-8");

        client = new OkHttpClient.Builder()
                .build();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("parlourid", item.getServiceId()); // here service id means parlour id
            jsonObject.put("subserviceid", item.getSubServiceId());
        } catch (Exception e) {
            Log.e(TAG, "hitApiAddToCart: " + e);
        }

        RequestBody body = RequestBody.create( JSON, jsonObject.toString() );
        request = new Request.Builder()
                .url( url )
                .header("Authorization", "Bearer " + getToken())
                .post(body)
                .build();
        client.newCall( request ).enqueue( new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity,"Network Error", Toast.LENGTH_LONG).show();
                    }
                });
                Log.e(TAG, "hitApiAddToCart: onFailure: " + e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()){
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "Service added to cart", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (response.code() == 409) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "Item already in cart", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.e(TAG, "hitApiAddToCart: onResponse: " + response.code());
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "Network error, try again later.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        } );
    }

    private String getToken(){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("access_token", null);
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

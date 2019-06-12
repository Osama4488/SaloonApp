package com.example.saloonapp.Adapters.User;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.saloonapp.Models.CartModel;
import com.example.saloonapp.R;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CartRecyclerViewAdapter extends RecyclerView.Adapter<CartRecyclerViewAdapter.ItemCartViewHolder> {

    private Activity activity;
    private List<CartModel> cartModelList;
    private AppCompatTextView  titleTV;
    private RecyclerView cartRV;
    private RelativeLayout bookingRL;

    //Api Strings
    private String url, TAG = "CART_RV_ADAPTER";
    private OkHttpClient client;
    private Request request;

    public CartRecyclerViewAdapter(Activity activity, List<CartModel> cartModelList, AppCompatTextView titleTV, RecyclerView cartRV, RelativeLayout bookingRL) {
        this.activity = activity;
        this.cartModelList = cartModelList;
        this.titleTV = titleTV;
        this.cartRV = cartRV;
        this.bookingRL = bookingRL;
    }

    @NonNull
    @Override
    public ItemCartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_cart, viewGroup, false);
        return new ItemCartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemCartViewHolder itemCartViewHolder, int position) {
        final CartModel item = cartModelList.get(position);
        if (item != null) {
            itemCartViewHolder.parlourNameTV.setText(item.getParlourName());
            itemCartViewHolder.parlourSubServiceNameTV.setText(item.getParlourServiceName()+ ": " + item.getParlourSubServiceName());
            itemCartViewHolder.parlourSubServicePriceTV.setText("Rs. " + item.getParlourSubServicePrice());
            itemCartViewHolder.plusIB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeQuantity(itemCartViewHolder.qtyTV, (+1), itemCartViewHolder.parlourSubServicePriceTV, item);
                }
            });
            itemCartViewHolder.minusIB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeQuantity(itemCartViewHolder.qtyTV, (-1), itemCartViewHolder.parlourSubServicePriceTV, item);
                }
            });
        }
    }

    private void changeQuantity(AppCompatTextView qtyTV, Integer value, AppCompatTextView priceTV, CartModel item) {
        Integer qty = Integer.valueOf(qtyTV.getText().toString());
        qty = qty + value;
        if (qty > 0) {
            if (qty > 5) {
                Toast.makeText(activity, "Service booking limit is upto 5 persons", Toast.LENGTH_SHORT).show();
            } else {
                Double valueToDouble = Double.parseDouble(item.getParlourSubServicePrice());
                Integer price = valueToDouble.intValue();
                price = price * qty;
                qtyTV.setText(qty.toString());
                priceTV.setText(price.toString());
            }
        } else {
            deleteDialog(item.getParlourSubServiceName(), cartModelList.indexOf(item));
        }
    }

    public void deleteDialog(final String subServiceName, final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle("Remove Service?");
        alertDialogBuilder.setMessage("Are you sure, You want to remove " + subServiceName.toUpperCase() + " from cart?");
        alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hitApiDeleteItemFromCart(position);
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

    private void hitApiDeleteItemFromCart(final int position) {
        url = activity.getString(R.string.url) + "carts/" + cartModelList.get(position).getCartId();

        client = new OkHttpClient.Builder()
                .build();

        request = new Request.Builder()
                .url( url )
                .header("Authorization", "Bearer " + getToken())
                .delete()
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
                Log.e(TAG, "hitApiDeleteItemFromCart: onFailure: " + e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()){
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, cartModelList.get(position).getParlourSubServiceName() + " removed", Toast.LENGTH_SHORT).show();
                            cartModelList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, cartModelList.size());
                            if (cartModelList.size() == 0) {
                                controlsVisibility(View.VISIBLE);
                            } else {
                                controlsVisibility(View.GONE);
                            }
                        }
                    });
                } else {
                    Log.e(TAG, "hitApiDeleteItemFromCart: onResponse: " + response.code());
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

    private void controlsVisibility(int visibliltiy) {
        if (View.VISIBLE == visibliltiy) {
            titleTV.setVisibility(View.VISIBLE);
            cartRV.setVisibility(View.GONE);
            bookingRL.setVisibility(View.GONE);
        } else {
            titleTV.setVisibility(View.GONE);
            cartRV.setVisibility(View.VISIBLE);
            bookingRL.setVisibility(View.VISIBLE);
        }
    }

    private String getToken(){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("access_token", null);
    }

    @Override
    public int getItemCount() {
        return cartModelList.size();
    }

    public class ItemCartViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView parlourNameTV, parlourSubServiceNameTV, parlourSubServicePriceTV, qtyTV;
        private AppCompatImageButton plusIB, minusIB;

        public ItemCartViewHolder(@NonNull View itemView) {
            super(itemView);

            parlourNameTV = itemView.findViewById(R.id.item_parlourNameTV);
            parlourSubServiceNameTV = itemView.findViewById(R.id.item_subServiceNameTV);
            parlourSubServicePriceTV = itemView.findViewById(R.id.item_subServicePriceTV);
            qtyTV = itemView.findViewById(R.id.item_qtyTV);
            plusIB = itemView.findViewById(R.id.item_plusIB);
            minusIB = itemView.findViewById(R.id.item_minusIB);
        }
    }
}

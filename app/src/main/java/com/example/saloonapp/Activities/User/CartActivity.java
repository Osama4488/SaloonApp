package com.example.saloonapp.Activities.User;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.saloonapp.Activities.Parlour.SubServiceActivity;
import com.example.saloonapp.Adapters.Parlour.ParlourScheduleRecyclerViewAdapter;
import com.example.saloonapp.Adapters.User.CartRecyclerViewAdapter;
import com.example.saloonapp.Adapters.User.CartRecyclerViewAdapter.ItemCartViewHolder;
import com.example.saloonapp.Models.CartModel;
import com.example.saloonapp.Models.ParlourModel;
import com.example.saloonapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CartActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private AppCompatTextView toolbarTitleTV, titleTV;
    private AppCompatButton bookinBN;
    private RecyclerView cartRV;
    private RelativeLayout bookingRL;

    private List<CartModel> cartModelList;

    //Api Strings
    private String url, TAG = "CART_ACTIVITY";
    private OkHttpClient client;
    private Request request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        bindControls();
        bindListeners();
        toolbarSetting();
        hitApiGetAllItemsInCart();
    }

    private void bindControls() {
        toolbar = findViewById(R.id.toolbar);
        toolbarTitleTV = toolbar.findViewById(R.id.toolbarTitleTV);
        titleTV = findViewById(R.id.cart_titleTV);
        cartRV = findViewById(R.id.cart_RV);
        bookinBN = findViewById(R.id.cart_bookingBN);
        bookingRL = findViewById(R.id.bookingRL);
    }

    private void bindListeners() {
        bookinBN.setOnClickListener(this);
    }

    private void toolbarSetting() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbarTitleTV.setText("CART");
    }

    private void hitApiGetAllItemsInCart() {
        url = getString(R.string.url) + "carts";

        client = new OkHttpClient.Builder()
                .build();

        request = new Request.Builder()
                .url( url )
                .header("Authorization", "Bearer " + getToken())
                .get()
                .build();

        client.newCall( request ).enqueue( new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
               runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CartActivity.this,"Network Error", Toast.LENGTH_LONG).show();
                    }
                });
                Log.e(TAG, "hitApiGetAllItemsInCart: onFailure:" + e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.code() == 200){
                    castCartItemsToList(response);
                } else {
                    Log.e(TAG, "hitApiGetAllItemsInCart: onResponse: " + response.code());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CartActivity.this, "Network error, try again later.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        } );
    }

    private void castCartItemsToList(Response response) {
        try {
            cartModelList = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(response.body().string());
            for (int i = 0; i < jsonArray.length(); i++) {
                cartModelList.add(new CartModel(
                        jsonArray.getJSONObject(i).getString("Id"),
                        jsonArray.getJSONObject(i).getString("Parlour"),
                        jsonArray.getJSONObject(i).getString("Service"),
                        jsonArray.getJSONObject(i).getString("SubServices"),
                        jsonArray.getJSONObject(i).getString("Price")
                ));
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setUpList();
                }
            });
        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(CartActivity.this, "Network error, try again later.", Toast.LENGTH_SHORT).show();
                }
            });
            Log.e(TAG, "castCartItemsToList: " + e);
        }
    }

    private String getToken(){
        SharedPreferences sharedPreferences = getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("access_token", null);
    }

    private void setUpList() {
        if (cartModelList.size() == 0) {
            controlsVisibility(View.VISIBLE);
        } else {
            controlsVisibility(View.GONE);
            CartRecyclerViewAdapter adapter = new CartRecyclerViewAdapter(CartActivity.this, cartModelList, titleTV, cartRV, bookingRL);
            cartRV.setHasFixedSize(true);
            cartRV.setLayoutManager(new LinearLayoutManager(this));
            cartRV.setAdapter(adapter);
        }
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

    @Override
    public void onClick(View v) {
        if (v == bookinBN) {
            hitApiCreateBooking();
        }
    }

    private void hitApiCreateBooking() {
        url = getString(R.string.url) + "bookings";

        client = new OkHttpClient.Builder()
                .build();

        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < cartModelList.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                ItemCartViewHolder holder = (ItemCartViewHolder) cartRV.findViewHolderForAdapterPosition(i);
                AppCompatTextView qtyTV = holder.itemView.findViewById(R.id.item_qtyTV);

                jsonObject.put("cartid", cartModelList.get(i).getCartId());
                jsonObject.put("quantity", qtyTV.getText().toString());

                jsonArray.put(jsonObject);
            }
        } catch (Exception e) {
            Log.e(TAG, "hitApiCreateBooking: " + e);
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create( JSON, jsonArray.toString() );

        request = new Request.Builder()
                .url( url )
                .header("Authorization", "Bearer " + getToken())
                .post(body)
                .build();
        client.newCall( request ).enqueue( new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CartActivity.this, "Network Error", Toast.LENGTH_LONG).show();
                    }
                });
                Log.e(TAG, "hitApiCreateBooking: onFailure: " + e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CartActivity.this, "Booking has been done.", Toast.LENGTH_SHORT).show();
                            cartModelList = new ArrayList<>();
                            setUpList();
                        }
                    });
                } else {
                    Log.e(TAG, "hitApiCreateBooking: onResponse: " + response.code());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CartActivity.this, "Network error, try again later.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        } );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}

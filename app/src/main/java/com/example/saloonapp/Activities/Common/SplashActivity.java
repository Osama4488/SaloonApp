package com.example.saloonapp.Activities.Common;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

import com.example.saloonapp.Activities.Parlour.ParlourDrawerActivity;
import com.example.saloonapp.Activities.User.UserDrawerActivity;
import com.example.saloonapp.R;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SplashActivity extends AppCompatActivity {

    private AppCompatImageView logoIV;
    private float growTo = 1.1f;
    private long duration = 1000;
    private String TAG = "SPLASH_ACTIVITY";

    //API strings
    private String url;
    private OkHttpClient client;
    private Request request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        bindControls();
        animateLogo();
        checkIfAlreadyLoggedIn();
    }

    private void bindControls() {
        logoIV = findViewById(R.id.logoIV);
    }

    private void animateLogo() {
        final ScaleAnimation grow = new ScaleAnimation(1, growTo, 1, growTo,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        grow.setDuration(duration / 2);
        ScaleAnimation shrink = new ScaleAnimation(growTo, 1, growTo, 1,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        shrink.setDuration(duration / 2);
        shrink.setStartOffset(duration / 2);
        final AnimationSet growAndShrink = new AnimationSet(true);
        growAndShrink.setInterpolator(new LinearInterpolator());
        growAndShrink.addAnimation(grow);
        growAndShrink.addAnimation(shrink);
        growAndShrink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                try {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            logoIV.startAnimation(growAndShrink);
                        }
                    },500);
                } catch (Exception e) {
                    Log.e(TAG, "onAnimationEnd: " + e );
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        logoIV.startAnimation(growAndShrink);
    }

    private void checkIfAlreadyLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("userDetails", MODE_PRIVATE);
        String email = sharedPreferences.getString("userName", null);
        String pass = sharedPreferences.getString("pass", null);
        if (email != null && pass != null) {
            hitApiLogin(email, pass);
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
            }, 2000);
        }
    }

    private void hitApiLogin(String email, final String pass) {
        url = getString(R.string.url_token) + "token";

        String urlURI = "username=" + email + "&Password=" + pass + "&grant_type=password";

        client = new OkHttpClient.Builder()
                .build();

        MediaType JSON = MediaType.parse( "application/x-www-form-urlencoded; charset=utf-8" );
        RequestBody body = RequestBody.create( JSON, urlURI );

        request = new Request.Builder()
                .url( url )
                .post( body )
                .build();

        client.newCall( request ).enqueue( new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText( getApplicationContext(), "Network error", Toast.LENGTH_LONG ).show();
                    }
                } );
                Log.e(TAG, "hitApiLogin: onFailure" + e);
            }

            @Override
            public void onResponse(Call call, final Response response) {
                if (response.code() == 200) {
                    saveUserData(response, pass);
                } else {
                    Log.e(TAG, "hitApiLogin: onResponse: " + response.code());
                    try {
                        final JSONObject jsonObject = new JSONObject(response.body().string());
                        final String errorMsg = jsonObject.getString("error_description");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SplashActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SplashActivity.this, "Network error, try again later.", Toast.LENGTH_LONG).show();
                            }
                        });
                        Log.e(TAG, "hitApiLogin: onResponse: " + e);
                    }
                }
            }
        } );
    }

    private void saveUserData(Response response, String pass) {
        try {
            JSONObject jsonObject = new JSONObject(response.body().string());
            SharedPreferences.Editor editor = getSharedPreferences("userDetails", MODE_PRIVATE).edit();
            editor.putString("access_token", jsonObject.getString("access_token"));
            editor.putString("roleName", jsonObject.getString("roleName"));
            editor.putString("userName", jsonObject.getString("userName"));
            editor.putString("fullName", jsonObject.getString("fullName"));
            editor.putString("rating", jsonObject.getString("rating"));
            editor.putString("pass", pass);
            editor.apply();

            if (jsonObject.getString("roleName").equals("Parlour")){
                startActivity(new Intent(SplashActivity.this, ParlourDrawerActivity.class));
            } else if (jsonObject.getString("roleName").equals("Client")) {
                startActivity(new Intent(SplashActivity.this, UserDrawerActivity.class));
            }

        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SplashActivity.this, "Network error, try again later", Toast.LENGTH_SHORT).show();
                }
            });
            Log.e(TAG, "saveUserData: " + e);
        }
    }
}

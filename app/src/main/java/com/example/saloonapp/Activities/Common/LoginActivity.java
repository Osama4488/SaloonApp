package com.example.saloonapp.Activities.Common;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.saloonapp.Activities.Parlour.ParlourDrawerActivity;
import com.example.saloonapp.Activities.User.UserDrawerActivity;
import com.example.saloonapp.R;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout emailTIL, passTIL;
    private AppCompatEditText emailET, passET;
    private RadioButton userRB, parlourRB;
    private AppCompatButton loginBN;
    private AppCompatTextView dontHaveAnAccountTV, userDrawer, parlourDrawer;

    //API strings
    private String url;
    private MediaType JSON;
    private OkHttpClient client;
    private Request request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bindControls();
        bindListeners();
        setEditTextError();
        disableCopyPaste();
    }

    private void bindControls() {
        emailTIL = findViewById(R.id.login_emailTIL);
        passTIL = findViewById(R.id.login_passTIL);

        emailET = findViewById(R.id.login_emailET);
        passET = findViewById(R.id.login_passET);

        userRB = findViewById(R.id.login_userRB);
        userRB.setChecked(true);
        parlourRB = findViewById(R.id.login_parlorRB);

        loginBN = findViewById(R.id.login_loginBN);

        dontHaveAnAccountTV = findViewById(R.id.login_signupTV);
        userDrawer = findViewById(R.id.userDrawer);
        parlourDrawer = findViewById(R.id.parlourDrawer);
    }

    private void bindListeners() {
        loginBN.setOnClickListener(this);
        userRB.setOnClickListener(this);
        parlourRB.setOnClickListener(this);
        dontHaveAnAccountTV.setOnClickListener(this);
        userDrawer.setOnClickListener(this);
        parlourDrawer.setOnClickListener(this);
    }

    private void setEditTextError() {
        emailET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = emailET.getText().toString();
                if (checkIsNullOrEmpty(email)) {
                    enableEdittextError(emailTIL, "Field can not be empty");
                } else {
                    if (isEmailAddressValid(email)) {
                        disableEditextError(emailTIL);
                    } else {
                        enableEdittextError(emailTIL, "Invalid email address");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        passET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pass = passET.getText().toString();
                if (checkIsNullOrEmpty(pass)) {
                    enableEdittextError(passTIL, "Field can not be empty");
                } else {
                    if (pass.length() < 8) {
                        enableEdittextError(passTIL, "Password length should be minimum 8 characters");
                    } else {
                        disableEditextError(passTIL);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    @Override
    public void onClick(View v) {
        if (v == loginBN) {
            loginMethod();
        } else if (v == dontHaveAnAccountTV) {
            startActivity(new Intent(getApplicationContext(), SignupActivity.class));
        } else if (v == userRB) {
            onRadioButtonClicked(userRB);
        } else if (v == parlourRB) {
            onRadioButtonClicked(parlourRB);
        } else if (v == userDrawer) {
            startActivity(new Intent(getApplicationContext(), UserDrawerActivity.class));
        } else if (v == parlourDrawer) {
            startActivity(new Intent(getApplicationContext(), ParlourDrawerActivity.class));
        }
    }

    private void loginMethod() {
        String email = emailET.getText().toString();
        String pass = passET.getText().toString();
        if (checkIsNullOrEmpty(email) || checkIsNullOrEmpty(pass)) {
            if (checkIsNullOrEmpty(email)) {
                enableEdittextError(emailTIL, "Field can not be empty");
            }
            if (checkIsNullOrEmpty(pass)) {
                enableEdittextError(passTIL, "Field can not be empty");
            }
        } else if (!(emailTIL.isErrorEnabled() || passTIL.isErrorEnabled())){
            hitApiLogin(email, pass);
        }
    }

    private void hitApiLogin(String email, String pass) {
        url = getString(R.string.url) + "token";

        JSON = MediaType.parse( "application/x-www-form-urlencoded; charset=utf-8" );

        String urlURI = "username=" + email + "&Password=" + pass + "&grant_type=password";

        client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
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
                Log.e("SERVER FAILURE", "hitApiLogin: " + e);
            }

            @Override
            public void onResponse(Call call, final Response response) {
                if (response.code() == 200) {
                    try {
                        saveUserData(response.body().string());
                    } catch (IOException e) {
                        Log.e("RESPONSE EXCEPTION", "hitApiLogin: onResponse: " + e );
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Network error, try again later.", Toast.LENGTH_SHORT).show();
                    Log.e("ANOTHER STATUS CODE", "hitApiLogin: " + response.code() );
                }
            }
        } );
    }

    private void saveUserData(String string) {
        //save data in shared preference
    }

    private boolean checkIsNullOrEmpty(String value) {
        if (value.isEmpty() || value.equals("")){
            return true;
        } else {
            return false;
        }
    }

    private void onRadioButtonClicked(View view) {
        if (view == userRB){
            userRB.setChecked(true);
            parlourRB.setChecked(false);
        } else if (view == parlourRB) {
            userRB.setChecked(false);
            parlourRB.setChecked(true);
        }

    }

    private void enableEdittextError(TextInputLayout view, String errorMsg){
        view.setErrorEnabled(true);
        view.setError(errorMsg);
    }

    private void disableEditextError(TextInputLayout view){
        view.setErrorEnabled(false);
    }

    private boolean isEmailAddressValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void disableCopyPaste() {
        passET.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode actionMode, MenuItem item) {
                return false;
            }

            public void onDestroyActionMode(ActionMode actionMode) {
            }
        });

        passET.setLongClickable(false);
        passET.setTextIsSelectable(false);
    }
}

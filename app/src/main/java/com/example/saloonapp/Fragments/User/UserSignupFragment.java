package com.example.saloonapp.Fragments.User;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.saloonapp.Activities.Common.LoginActivity;
import com.example.saloonapp.Activities.Parlour.ParlourScheduleActivity;
import com.example.saloonapp.R;

import org.json.JSONArray;
import org.json.JSONException;
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

public class UserSignupFragment extends Fragment implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private TextInputLayout nameTIL, emailTIL, contactTIL, passTIL, confirmPassTIL;
    private AppCompatEditText nameET, emailET, contactET, passET, confirmPassET;
    private AppCompatButton signupBN;

    //API strings
    private String url;
    private MediaType JSON;
    private OkHttpClient client;
    private Request request;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_signup, container, false);
        bindControls(view);
        bindListeners();
        setEditttexError();
        disableCopyPaste();
        return view;
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

        confirmPassET.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

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

        confirmPassET.setLongClickable(false);
        confirmPassET.setTextIsSelectable(false);
    }

    private void setEditttexError() {
        nameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String name = nameET.getText().toString();
                if (checkIsNullOrEmpty(name)) {
                    enableEdittextError(emailTIL, "Field can not be empty");
                } else {
                    if (name.length() < 3) {
                        enableEdittextError(nameTIL, "Name must be of minimum 3 characters");
                    } else {
                        disableEditextError(nameTIL);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
        contactET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String contact = contactET.getText().toString();
                if (checkIsNullOrEmpty(contact)) {
                    enableEdittextError(contactTIL, "Field can not be empty");
                } else {
                    if (contact.length() == 11) {
                        disableEditextError(contactTIL);
                    } else {
                        enableEdittextError(contactTIL, "Number must be of minimun 11 characters");
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
                        enableEdittextError(passTIL, "Password must be of minimum 8 characters");
                    } else {
                        disableEditextError(passTIL);
                    }
                    if (confirmPassET.getText().toString().equals(pass)) {
                        disableEditextError(confirmPassTIL);
                    } else {
                        enableEdittextError(confirmPassTIL, "Passwords do not match");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        confirmPassET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String confirmPass = confirmPassET.getText().toString();
                if (checkIsNullOrEmpty(confirmPass)) {
                    enableEdittextError(confirmPassTIL, "Field can not be empty");
                } else {
                    if (passET.getText().toString().equals(confirmPass)) {
                        disableEditextError(confirmPassTIL);
                    } else {
                        enableEdittextError(confirmPassTIL, "Passwords do not match");
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void bindControls(View view) {
        nameTIL = view.findViewById(R.id.user_signup_frag_nameTIL);
        emailTIL = view.findViewById(R.id.user_signup_frag_emailTIL);
        contactTIL = view.findViewById(R.id.user_signup_frag_contactTIL);
        passTIL = view.findViewById(R.id.user_signup_frag_passTIL);
        confirmPassTIL = view.findViewById(R.id.user_signup_frag_confrimPassTIL);

        nameET = view.findViewById(R.id.user_signup_frag_nameET);
        emailET = view.findViewById(R.id.user_signup_frag_emailET);
        contactET = view.findViewById(R.id.user_signup_frag_contactET);
        passET = view.findViewById(R.id.user_signup_frag_passET);
        confirmPassET = view.findViewById(R.id.user_signup_frag_confrimPassET);

        signupBN = view.findViewById(R.id.user_signup_frag_signupBN);
    }

    private void bindListeners() {
        signupBN.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        if (v == signupBN) {
            userSignupMethod();
        }
    }

    private void userSignupMethod() {
        String name = nameET.getText().toString();
        String email = emailET.getText().toString();
        String contact = contactET.getText().toString();
        String pass = passET.getText().toString();
        String confirmPass = confirmPassET.getText().toString();

        if (checkIsNullOrEmpty(name) || checkIsNullOrEmpty(email) || checkIsNullOrEmpty(contact) || checkIsNullOrEmpty(pass) || checkIsNullOrEmpty(confirmPass) ) {
            if (checkIsNullOrEmpty(name)) {
                enableEdittextError(nameTIL, "Field can not be empty");
            }
            if (checkIsNullOrEmpty(email)) {
                enableEdittextError(emailTIL, "Field can not be empty");
            }
            if (checkIsNullOrEmpty(contact)) {
                enableEdittextError(contactTIL, "Field can not be empty");
            }
            if (checkIsNullOrEmpty(pass)) {
                enableEdittextError(passTIL, "Field can not be empty");
            }
            if (checkIsNullOrEmpty(name)) {
                enableEdittextError(confirmPassTIL, "Passwords do not match");
            }
        } else if (!(nameTIL.isErrorEnabled() && emailTIL.isErrorEnabled() && contactTIL.isErrorEnabled() && passTIL.isErrorEnabled() && confirmPassTIL.isErrorEnabled())) {
            hitApiRegisterClient(name, email, contact, pass, confirmPass);
        }
    }

    private void hitApiRegisterClient(String name, String email, String contact, String pass, String confirmPass) {
        url = getString(R.string.url) + "account/registerclient";

        JSON = MediaType.parse( "application/json; charset=utf-8" );

        client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("FullName", name);
            jsonObject.put("Email", email);
            jsonObject.put("Phone", contact);
            jsonObject.put("Password", pass);
            jsonObject.put("ConfirmPassword", confirmPass);
        } catch (JSONException e) {
            Log.e("JSON EXCEPTION", "hitApiRegisterClient: " + e);
        }

        RequestBody body = RequestBody.create( JSON, jsonObject.toString() );
        request = new Request.Builder()
                .url( url )
                .post( body )
                .build();

        client.newCall( request ).enqueue( new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("SERVER FAILURE", "hitApiRegisterClient: " + e);
            }

            @Override
            public void onResponse(Call call, final Response response) {
                if (response.code() == 200){
                    getActivity().finishAffinity();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    Toast.makeText( getActivity(), "User account successfully created. Login to continue", Toast.LENGTH_LONG).show();
                }
                else {
                    try {
                        Log.e("ANOTHER STATUS CODE", "hitApiRegisterClient: onResponse: " + response.code() );
                        JSONObject serverResponse = new JSONObject(response.body().string());
                        final JSONArray errorMsg = serverResponse.getJSONObject("ModelState").getJSONArray("");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Toast.makeText(getActivity(), errorMsg.get(1).toString(), Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (Exception e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "Network error, try again later.", Toast.LENGTH_LONG).show();
                            }
                        });
                        Log.e("RESPONSE EXCEPTION", "hitApiRegisterClient: onResponse: " + e);
                    }
                }
            }
        } );
    }

    private boolean checkIsNullOrEmpty(String value) {
        if (value.isEmpty() || value.equals("")){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}

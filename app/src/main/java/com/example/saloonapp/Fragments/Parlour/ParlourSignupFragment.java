package com.example.saloonapp.Fragments.Parlour;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.saloonapp.Activities.Parlour.MapActivity;
import com.example.saloonapp.Activities.Parlour.ParlourScheduleActivity;
import com.example.saloonapp.R;

public class ParlourSignupFragment extends Fragment implements View.OnClickListener {

    private TextInputLayout nameTIL, emailTIL, contactTIL, locationTIL, passTIL, confirmPassTIL;
    private AppCompatEditText nameET, emailET, contactET, locationET, passET, confirmPassET;
    private AppCompatButton signupBN;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parlour_signup, container, false);

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
        locationET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String location = locationET.getText().toString();
                if (checkIsNullOrEmpty(location)) {
                    enableEdittextError(locationTIL, "Field can not be empty");
                } else {
                    disableEditextError(locationTIL);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (checkIsNullOrEmpty(s.toString())) {
                    enableEdittextError(locationTIL, "Field can not be empty");
                } else {
                    disableEditextError(locationTIL);
                }
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
        nameTIL = view.findViewById(R.id.parlour_signup_frag_nameTIL);
        emailTIL = view.findViewById(R.id.parlour_signup_frag_emailTIL);
        contactTIL = view.findViewById(R.id.parlour_signup_frag_contactTIL);
        locationTIL = view.findViewById(R.id.parlour_signup_frag_locationTIL);
        passTIL = view.findViewById(R.id.parlour_signup_frag_passTIL);
        confirmPassTIL = view.findViewById(R.id.parlour_signup_frag_confrimPassTIL);

        nameET = view.findViewById(R.id.parlour_signup_frag_nameET);
        emailET = view.findViewById(R.id.parlour_signup_frag_emailET);
        contactET = view.findViewById(R.id.parlour_signup_frag_contactET);
        locationET = view.findViewById(R.id.parlour_signup_frag_locationET);
        passET = view.findViewById(R.id.parlour_signup_frag_passET);
        confirmPassET = view.findViewById(R.id.parlour_signup_frag_confrimPassET);

        signupBN = view.findViewById(R.id.parlour_signup_frag_signupBN);
    }

    private void bindListeners() {
        signupBN.setOnClickListener(this);
        locationET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MapActivity.class));
            }
        });
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
            parlourSignupMethod();
        }
    }

    private void parlourSignupMethod() {
        String name = nameET.getText().toString();
        String email = emailET.getText().toString();
        String contact = contactET.getText().toString();
        String location = locationET.getText().toString();
        String pass = passET.getText().toString();
        String confirmPass = confirmPassET.getText().toString();

        if (checkIsNullOrEmpty(name) || checkIsNullOrEmpty(email) || checkIsNullOrEmpty(contact) || checkIsNullOrEmpty(location) || checkIsNullOrEmpty(pass) || checkIsNullOrEmpty(confirmPass) ) {
            if (checkIsNullOrEmpty(name)) {
                enableEdittextError(nameTIL, "Field can not be empty");
            }
            if (checkIsNullOrEmpty(email)) {
                enableEdittextError(emailTIL, "Field can not be empty");
            }
            if (checkIsNullOrEmpty(contact)) {
                enableEdittextError(contactTIL, "Field can not be empty");
            }
            if (checkIsNullOrEmpty(location)) {
                enableEdittextError(locationTIL, "Field can not be empty");
            }
            if (checkIsNullOrEmpty(pass)) {
                enableEdittextError(passTIL, "Field can not be empty");
            }
            if (checkIsNullOrEmpty(name)) {
                enableEdittextError(confirmPassTIL, "Passwords do not match");
            }
        } else if (!(nameTIL.isErrorEnabled() && emailTIL.isErrorEnabled() && contactTIL.isErrorEnabled() && locationTIL.isErrorEnabled() && passTIL.isErrorEnabled() && confirmPassTIL.isErrorEnabled())) {
            Intent intent = new Intent(getActivity(), ParlourScheduleActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("email", email);
            intent.putExtra("contact", contact);
            intent.putExtra("location", location);
            intent.putExtra("pass", pass);
            intent.putExtra("confirmPass", confirmPass);
            getActivity().startActivity(intent);
        }
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

    @Override
    public void onResume() {
        SharedPreferences mapLatLng = getActivity().getSharedPreferences("MapLatLng", Context.MODE_PRIVATE);
        Float lat = mapLatLng.getFloat("lat", 0);
        Float lng = mapLatLng.getFloat("lng", 0);
        Log.e("VALUES", "onResume: " + lat.toString() + ", " + lng.toString() );
        if (lat != 0 && lng != 0) {
            locationET.setText(lat.toString() + ", " + lng.toString());
            SharedPreferences.Editor editor = getActivity().getSharedPreferences("MapLatLng", Context.MODE_PRIVATE).edit();
            editor.clear().apply();
        }
        super.onResume();
    }
}

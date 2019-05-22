package com.example.saloonapp.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saloonapp.Adapters.PlaceAutocompleteAdapter;
import com.example.saloonapp.Models.PlaceInfo;
import com.example.saloonapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private GoogleMap mMap;
    private static final String Tag = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    private Boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlaceAutocompleteAdapter mplaceAutoCompeleteAdapter;
    private LatLng saveLatLng;

    private GoogleApiClient mGoogleApiClient;
    private PlaceInfo mPlace;

    private AutoCompleteTextView searchATV;
    private AppCompatImageView backIV;
    private RelativeLayout backAndSearchRL;
    private AppCompatButton okBN;
    private FloatingActionButton currentLocationFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        bindControls();
        bindListeners();
        getLocationPermission();
        setMap();
    }

    @SuppressLint("RestrictedApi")
    private void bindControls() {
        backAndSearchRL = findViewById(R.id.map_backAndSearchRL);
        searchATV = findViewById(R.id.map_searchATV);
        currentLocationFAB = findViewById(R.id.map_currentLocationFAB);
        okBN = findViewById(R.id.map_okBN);
        backIV = findViewById(R.id.map_bacKIV);

        currentLocationFAB.setVisibility(View.INVISIBLE);
        backAndSearchRL.bringToFront();
    }

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                setMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void setMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mLocationPermissionsGranted) {
            getCurrentLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onCameraMove() {
                    currentLocationFAB.setVisibility(View.VISIBLE);
                }
            });

            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    setMarkerAndMoveCamera(marker.getPosition(), null);
                }
            });

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    setMarkerAndMoveCamera(latLng, null);
                }
            });
        }
    }

    private void setMarkerAndMoveCamera(LatLng latLng, PlaceInfo placeInfo){
        mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
        MarkerOptions options = new MarkerOptions()
                .position(latLng).draggable(true)
                .title("You are here!");
        mMap.addMarker(options);
        saveLatLng = latLng;
    }

    private void getCurrentLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionsGranted) {

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(Tag, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            setMarkerAndMoveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),null);
                        } else {
                            Log.d(Tag, "onComplete: current location is null");
                            Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(Tag, "getCurrentLocation: SecurityException: " + e.getMessage());
        }
    }

    private void bindListeners() {
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        searchATV.setOnItemClickListener(mAutocompleteClickListener);
        mplaceAutoCompeleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient,
                LAT_LNG_BOUNDS, null);




        searchATV.setAdapter(mplaceAutoCompeleteAdapter);
        searchATV.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                    //execute our method for searching
                    geoLocate();
                }

                return false;
            }
        });

        currentLocationFAB.setOnClickListener(this);
        okBN.setOnClickListener(this);
        backIV.setOnClickListener(this);

        hideSoftKeyboard();
    }

    private void geoLocate() {
        String searchString = searchATV.getText().toString();

        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e(Tag, "geoLocate: IOException: " + e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);

            setMarkerAndMoveCamera(new LatLng(address.getLatitude(), address.getLongitude()), null);
        }
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            hideSoftKeyboard();

            final AutocompletePrediction item = mplaceAutoCompeleteAdapter.getItem(i);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()){
                Log.d(Tag, "onResult: Place query did not complete successfully: " + places.getStatus().toString());
                places.release();
                return;
            }
            final Place place = places.get(0);

            try{
                mPlace = new PlaceInfo();
                mPlace.setName(place.getName().toString());
                Log.d(Tag, "onResult: name: " + place.getName());
                mPlace.setAddress(place.getAddress().toString());
                Log.d(Tag, "onResult: address: " + place.getAddress());
                mPlace.setAttributions(place.getAttributions().toString());
//                Log.d(TAG, "onResult: attributions: " + place.getAttributions());
                mPlace.setId(place.getId());
                Log.d(Tag, "onResult: id:" + place.getId());
                mPlace.setLatlng(place.getLatLng());
                Log.d(Tag, "onResult: latlng: " + place.getLatLng());
                mPlace.setRating(place.getRating());
                Log.d(Tag, "onResult: rating: " + place.getRating());
                mPlace.setPhoneNumber(place.getPhoneNumber().toString());
                Log.d(Tag, "onResult: phone number: " + place.getPhoneNumber());
                mPlace.setWebsiteUri(place.getWebsiteUri());
                Log.d(Tag, "onResult: website uri: " + place.getWebsiteUri());
                Log.d(Tag, "onResult: place: " + mPlace.toString());
            }catch (NullPointerException e){
                Log.e(Tag, "onResult: NullPointerException: " + e.getMessage() );
            }
            setMarkerAndMoveCamera(new LatLng(place.getViewport().getCenter().latitude,
                    place.getViewport().getCenter().longitude), mPlace);

            places.release();
        }
    };

    private void hideSoftKeyboard(){
        // this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchATV.getWindowToken(), 0);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View v) {
        if (v == okBN) {
            saveLatLng();
            finish();
        } else if (v == backIV) {
            onBackPressed();
        } else if (v == currentLocationFAB) {
            currentLocationFAB.setVisibility(View.INVISIBLE);
            getCurrentLocation();
        }
    }

    private void saveLatLng() {
        SharedPreferences.Editor editor = getSharedPreferences("MapLatLng", MODE_PRIVATE).edit();
        editor.putFloat("lat", (float) saveLatLng.latitude);
        editor.putFloat("lng", (float) saveLatLng.longitude);
        editor.apply();
        Toast.makeText(this, saveLatLng.latitude + " : " + saveLatLng.longitude, Toast.LENGTH_SHORT).show();

    }
}

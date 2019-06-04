package com.example.saloonapp.Activities.User;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.saloonapp.Adapters.User.ParlourRecyclerViewAdapter;
import com.example.saloonapp.Models.ParlourModel;
import com.example.saloonapp.R;

import java.util.ArrayList;
import java.util.List;

public class ListOfParloursActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private AppCompatTextView toolbarTitleTV;
    private RecyclerView parlourRV;

    private String title;
    private List<ParlourModel> parlourModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_parlours);

        getValuesOnStart();
        bindControls();
        toolbarSetting();
        setUpList();
    }

    private void getValuesOnStart() {
        title = getIntent().getExtras().getString("title", "not found");
        parlourModelList = (List<ParlourModel>) getIntent().getSerializableExtra("list");
    }

    private void bindControls() {
        toolbar = findViewById(R.id.toolbar);
        toolbarTitleTV = toolbar.findViewById(R.id.toolbarTitleTV);

        parlourRV = findViewById(R.id.list_parlourRV);
    }

    private void toolbarSetting() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbarTitleTV.setText(title.toUpperCase());
    }

    private void setUpList() {
        ParlourRecyclerViewAdapter adapter = new ParlourRecyclerViewAdapter(ListOfParloursActivity.this, parlourModelList, R.layout.item_parlour_vertical);
        parlourRV.setHasFixedSize(true);
        parlourRV.setLayoutManager(new LinearLayoutManager(ListOfParloursActivity.this));
        parlourRV.setAdapter(adapter);
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

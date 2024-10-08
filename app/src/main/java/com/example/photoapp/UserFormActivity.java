package com.example.photoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import androidx.appcompat.app.AppCompatActivity;

public class UserFormActivity  extends AppCompatActivity {

    String[]  activity = {"GateIn","Gate Out"};
    String[]  containerType = {"GateIn","Gate Out"};
    String[]  containerSize = {"20","40"};
    String[]  locations = {"Dadri","Mundra 1","Mundra 2"};
    String[]  surveyors = {"Durga","Varsha","Ganesh"};
    String[]  containerStatus = {"Damaged","Gate Out"};

    ArrayAdapter<String> adapterItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_form);

        //Activity
        AutoCompleteTextView activityView = findViewById(R.id.autoCompleteTextViewActivity);
        setDropdowns(activityView,activity);

        //Container Type
        AutoCompleteTextView conTypeView = findViewById(R.id.autoComTVContTypeList);
        setDropdowns(conTypeView,containerType);

        //Container Size
        AutoCompleteTextView conSizeView = findViewById(R.id.autoComTVContSizeList);
        setDropdowns(conSizeView,containerSize);

        //Locations
        AutoCompleteTextView locView= findViewById(R.id.autoComTVLocationList);
        setDropdowns(locView,locations);
        //Surveyors

        AutoCompleteTextView surveyorView = findViewById(R.id.autoComTVSurveyorNameList);
        setDropdowns(surveyorView,surveyors);

        //Container Status
        AutoCompleteTextView conStatusView= findViewById(R.id.autoComTVConStatusList);
        setDropdowns(conStatusView,containerStatus);

        activityView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String item = adapterView.getItemAtPosition(position).toString();
                activityView.setText(item, false);
            }
        });

        conTypeView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String item = adapterView.getItemAtPosition(position).toString();
                conTypeView.setText(item, false);
            }
        });

        conSizeView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String item = adapterView.getItemAtPosition(position).toString();
                conSizeView.setText(item, false);
            }
        });

        locView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String item = adapterView.getItemAtPosition(position).toString();
                locView.setText(item, false);
            }
        });

        surveyorView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String item = adapterView.getItemAtPosition(position).toString();
                surveyorView.setText(item, false);
            }
        });

        conStatusView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String item = adapterView.getItemAtPosition(position).toString();
                conStatusView.setText(item, false);
            }
        });

    }

    private void setDropdowns(AutoCompleteTextView autoCompleteTextView,String[] values) {
        adapterItems = new ArrayAdapter<String>(this,R.layout.list_item,values);
        autoCompleteTextView.setAdapter(adapterItems);
    }

    public void moveToNext(View view) {
        Intent intent = new Intent(UserFormActivity.this, DamagedContainerActivity.class);
        startActivity(intent);
    }
}

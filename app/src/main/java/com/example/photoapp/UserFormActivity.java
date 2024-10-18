package com.example.photoapp;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class UserFormActivity  extends AppCompatActivity {

    String[]  activity = {"GateIn","Gate Out"};
    String[]  containerType = {"GateIn","Gate Out"};
    String[]  containerSize = {"20","40"};
    String[]  locations = {"Dadri","Mundra 1","Mundra 2"};
    String[]  surveyors = {"Durga","Varsha","Ganesh"};
    String[]  containerStatus = {"Damaged","Gate Out"};

    ArrayAdapter<String> adapterItems;
    RecyclerView recyclerView;
    RecycleAdapter adapter;
    ImageView icon;
    ArrayList<Uri> uri = new ArrayList<Uri>();
    private static  final int Read_Permission = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_form);

        icon = findViewById(R.id.FolderIconImageView);
        recyclerView = findViewById(R.id.recyclerView_Gallery_Images);

        adapter = new RecycleAdapter(uri);
        recyclerView.setLayoutManager(new GridLayoutManager(UserFormActivity.this,3));
        recyclerView.setAdapter(adapter);

        if(ContextCompat.checkSelfPermission(UserFormActivity.this,android.Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(UserFormActivity.this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    Read_Permission);
        }

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
                {
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                }
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),1);
            }
        });

        //Container Number
        EditText con_No_EditText = findViewById(R.id.containerNumber);
        Intent intent = getIntent();
        con_No_EditText.setText(intent.getStringExtra("cno"));

        //Time
        EditText DateTime = findViewById(R.id.dateTime);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
        DateTime.setText(dtf.format(now));
        DateTime.setEnabled(false);

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
                if(item.equals("Damaged"))
                {
                    Intent intent = new Intent(UserFormActivity.this, DamagedContainerActivity.class);
                    startActivity(intent);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 & resultCode == Activity.RESULT_OK){
            if(data.getClipData()!=null)
            {
                int x= data.getClipData().getItemCount();
                for(int i=0;i<x;i++){
                    uri.add(data.getClipData().getItemAt(i).getUri());
                }
                adapter.notifyDataSetChanged();
            }
            if(data.getData()!=null){
                String imageURL = data.getData().getPath();
                uri.add(Uri.parse(imageURL));
            }
        }
    }

    private void setDropdowns(AutoCompleteTextView autoCompleteTextView, String[] values) {
        adapterItems = new ArrayAdapter<String>(this,R.layout.list_item,values);
        autoCompleteTextView.setAdapter(adapterItems);
    }

    public void moveToNext(View view) {
        Intent intent = new Intent(UserFormActivity.this, DamagedContainerActivity.class);
        startActivity(intent);
    }
}

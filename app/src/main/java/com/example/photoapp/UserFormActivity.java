package com.example.photoapp;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Objects;

public class UserFormActivity  extends AppCompatActivity {

    String[]  activity = {"GateIn","Gate Out","Import","Export","Empty"};
    String[]  containerType = {"GateIn","Gate Out"};
    String[]  containerSize = {"20","40","45"};
    String[]  locations = {"Dadri","Mundra 1","Mundra 2"};
    String[]  surveyors = {"Durga","Varsha","Ganesh"};
    String[]  containerStatus = {"Damaged","Sound"};
    ArrayList<String>  containerNos = new ArrayList<String>();

    private Dialog dialog;
    ArrayAdapter<String> adapterItems;
    RecyclerView recyclerView;
    RecycleAdapter adapter;
    ImageView galleryIcon,cameraIcon;
    ArrayList<Uri> uri = new ArrayList<Uri>();
    private static  final int Read_Permission = 101;

    EditText DateTime;
    Uri cam_uri;
    AutoCompleteTextView containerNumberView,activityView,conTypeView,conSizeView,locView,surveyorView, conStatusView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_form);

        containerNumberView = findViewById(R.id.autoCompleteTextContainerNo);
        DateTime = findViewById(R.id.dateTime);
        activityView  = findViewById(R.id.autoCompleteTextViewActivity);
        conTypeView= findViewById(R.id.autoComTVContTypeList);
        conSizeView= findViewById(R.id.autoComTVContSizeList);
        locView= findViewById(R.id.autoComTVLocationList);
        surveyorView= findViewById(R.id.autoComTVSurveyorNameList);
        conStatusView= findViewById(R.id.autoComTVConStatusList);

        galleryIcon = findViewById(R.id.FolderIconImageView);
        cameraIcon = findViewById(R.id.cameraIconImageView);
        recyclerView = findViewById(R.id.recyclerView_Gallery_Images);

        adapter = new RecycleAdapter(uri);
        recyclerView.setLayoutManager(new GridLayoutManager(UserFormActivity.this,2));
        recyclerView.setAdapter(adapter);

        if(ContextCompat.checkSelfPermission(UserFormActivity.this,android.Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(UserFormActivity.this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    Read_Permission);
        }

        galleryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),1);
            }
        });

        cameraIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
                cam_uri = getApplicationContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cam_uri);
                startActivityForResult(cameraIntent,123);
            }
        });

        //Container Number
        Intent intent = getIntent();
        containerNos = intent.getStringArrayListExtra("cno");
        assert containerNos != null;
        if(!containerNos.isEmpty())
        {
            containerNumberView.setText(containerNos.get(0));
        }

        //Time
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
        DateTime.setText(dtf.format(now));
        DateTime.setEnabled(false);

        //Container Number
        String[] cnos = new String[containerNos.size()];
        cnos = containerNos.toArray(cnos);
        setDropdowns(containerNumberView, cnos);

        //Activity
        setDropdowns(activityView,activity);

        //Container Type
        setDropdowns(conTypeView,containerType);

        //Container Size
        setDropdowns(conSizeView,containerSize);

        //Locations
        setDropdowns(locView,locations);
        //Surveyors
        setDropdowns(surveyorView,surveyors);

        //Container Status
        setDropdowns(conStatusView,containerStatus);

        containerNumberView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String item = adapterView.getItemAtPosition(position).toString();
                containerNumberView.setText(item, false);
            }
        });

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
                //custom Toast
                LayoutInflater inflater = getLayoutInflater();
                View customToastLayout = inflater.inflate(R.layout.activity_custom_toast,(ViewGroup) findViewById(R.id.custom_toast_container));
                TextView txtMessage = customToastLayout.findViewById(R.id.text);
                Toast mToast = new Toast(getApplicationContext());
                mToast.setDuration(Toast.LENGTH_LONG);
                mToast.setView(customToastLayout);

                if(item.equals("Damaged"))
                {
                    if(CheckAllFields()) {
                        CameraActivity camAct = new CameraActivity();
                        if(camAct.isValidContainerNumber(containerNumberView.getText().toString()))
                        {
                            conStatusView.setText(item, false);
                            dialog = createDialog("Damaged");
                            TextView t = dialog.findViewById(R.id.conMsg);
                            t.setText(containerNumberView.getText().toString());
                            dialog.show();
                        }
                        else
                        {
                            txtMessage.setText("Please enter the valid Container number");
                            mToast.show();
                        }

                    }
                    else {
                        conStatusView.setText("");
                        txtMessage.setText("All Fields are required");
                        mToast.show();
                    }
                }
            }
        });
    }

    public Dialog createDialog(String conStatus)
    {
        //Dialog to confirm the Container No
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_layout);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_background));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

        Button Okay = dialog.findViewById(R.id.btn_okay);
        Button Cancel = dialog.findViewById(R.id.btn_cancel);

        Okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if(conStatus.equals("Damaged"))
                {
                    intent = new Intent(UserFormActivity.this, DamagedContainerActivity.class);
                }
                else
                {
                    intent = new Intent(UserFormActivity.this, MainActivity.class);
                }

                startActivity(intent);
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        return dialog;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if((requestCode==1 || requestCode == 123) & resultCode == Activity.RESULT_OK){
            if(data!=null)
            {
                if(data.getClipData()!=null)
                {
                    int x= data.getClipData().getItemCount();
                    for(int i=0;i<x;i++){
                        uri.add(data.getClipData().getItemAt(i).getUri());
                        adapter.notifyDataSetChanged();
                    }
                }
                else if(data.getData()!=null) {
                    String imageURL = data.getData().getPath();
                    uri.add(Uri.parse(imageURL));
                }
            }
            else
            {
                uri.add(cam_uri);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void setDropdowns(AutoCompleteTextView autoCompleteTextView, String[] values) {
        adapterItems = new ArrayAdapter<String>(this,R.layout.list_item,values);
        autoCompleteTextView.setAdapter(adapterItems);
    }

    public void moveToNext(View view) {

        LayoutInflater inflater = getLayoutInflater();
        View customToastLayout = inflater.inflate(R.layout.activity_custom_toast,(ViewGroup) findViewById(R.id.custom_toast_container));
        TextView txtMessage = customToastLayout.findViewById(R.id.text);
        Toast mToast = new Toast(getApplicationContext());
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.setView(customToastLayout);

        if(CheckAllFields())
        {
            if(recyclerView.getAdapter().getItemCount()>0)
            {
                CameraActivity camAct = new CameraActivity();
                if(camAct.isValidContainerNumber(containerNumberView.getText().toString()))
                {
                    dialog = createDialog("Sound");
                    TextView t = dialog.findViewById(R.id.conMsg);
                    t.setText(containerNumberView.getText().toString());
                    dialog.show();
                }
                else
                {
                    txtMessage.setText("Please enter the valid Container number");
                    mToast.show();
                }
            }
            else
            {
                txtMessage.setText("Please upload atleast 1 image to continue");
                mToast.show();
            }
        }
        else {
            txtMessage.setText("All Fields are required");
            mToast.show();
        }

    }

    private boolean CheckAllFields() {
        boolean retValue = true;
        if(DateTime.length() == 0) {
            return false;
        }

        if(containerNumberView.getText().toString().isEmpty() || activityView.getText().toString().isEmpty() || locView.getText().toString().isEmpty()  || surveyorView.getText().toString().isEmpty()  || conTypeView.getText().toString().isEmpty()  || conSizeView.getText() .toString().isEmpty() || conStatusView.getText().toString().isEmpty())
        {
            return false;
        }
        return retValue;
    }
}

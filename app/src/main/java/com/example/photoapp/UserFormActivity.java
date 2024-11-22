package com.example.photoapp;
import android.Manifest;
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

import com.google.firebase.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserFormActivity  extends AppCompatActivity {

    String[]  activity = {"Import GateIn","Import GateOut","Export GateIn","Export GateOut","Empty GateIn","Empty GateOut"};
    String[]  containerType = {"Standard","Refrigerated","OpenTop"};
    String[]  containerSize = {"20","40","45"};
    String[]  locations = {"Dadri","Mundra_1","Mundra_2","Kolkata","Veshvi","Mundra Empty Park"};
    ArrayList<String>  surveyors = new ArrayList<String>();
    String[]  containerStatus = {"Damaged","Sound"};
    String[]  shippingLine = {"ML","MSC","CCG","OOCL"};
    ArrayList<String>  containerNos = new ArrayList<String>();
    String[] loadedSurveyors;

    private Dialog dialog;
    ArrayAdapter<String> adapterItems;
    RecyclerView recyclerView;
    RecycleAdapter adapter;
    ImageView galleryIcon,cameraIcon;
    ArrayList<Uri> uri = new ArrayList<Uri>();
    private static  final int Read_Permission = 101;

    EditText dateTime;
    Uri cam_uri;
    AutoCompleteTextView shippingLineTextView,containerNumberView,activityView,conTypeView,conSizeView,locView,surveyorView, conStatusView;
    TextView remark ;
    String selectedLocation;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_form);

        containerNumberView = findViewById(R.id.autoCompleteTextContainerNo);
        dateTime = findViewById(R.id.dateTime);
        activityView  = findViewById(R.id.autoCompleteTextViewActivity);
        conTypeView= findViewById(R.id.autoComTVContTypeList);
        conSizeView= findViewById(R.id.autoComTVContSizeList);
        locView= findViewById(R.id.autoComTVLocationList);
        surveyorView= findViewById(R.id.autoComTVSurveyorNameList);
        conStatusView= findViewById(R.id.autoComTVConStatusList);
        remark = findViewById(R.id.remarkTextView);
        shippingLineTextView = findViewById(R.id.autoComShippingLine);

        galleryIcon = findViewById(R.id.FolderIconImageView);
        cameraIcon = findViewById(R.id.cameraIconImageView);
        recyclerView = findViewById(R.id.recyclerView_Gallery_Images);

        adapter = new RecycleAdapter(uri);
        recyclerView.setLayoutManager(new GridLayoutManager(UserFormActivity.this,2));
        recyclerView.setAdapter(adapter);

        loadSurveyorList();
        if(ContextCompat.checkSelfPermission(UserFormActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(UserFormActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
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
        dateTime.setText(dtf.format(now));
        dateTime.setEnabled(false);

        //Container Number
        String[] cnos = new String[containerNos.size()];
        cnos = containerNos.toArray(cnos);
        setDropdowns(containerNumberView, cnos);

        //Activity
        setDropdowns(activityView,activity);

        //Shipping Line
        setDropdowns(shippingLineTextView,shippingLine);

        //Container Type
        setDropdowns(conTypeView,containerType);

        //Container Size
        setDropdowns(conSizeView,containerSize);

        //Locations
        setDropdowns(locView,locations);

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

        locView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String item = adapterView.getItemAtPosition(position).toString();
                locView.setText(item, false);
                selectedLocation = item;
                loadSurveyorList();
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
                View customToastLayout = inflater.inflate(R.layout.activity_custom_toast, (ViewGroup) findViewById(R.id.custom_toast_container));
                TextView txtMessage = customToastLayout.findViewById(R.id.text);
                Toast mToast = new Toast(getApplicationContext());
                mToast.setDuration(Toast.LENGTH_LONG);
                mToast.setView(customToastLayout);

                if (item.equals("Damaged")) {
                    if (CheckAllFields()) {
                        CameraActivity camAct = new CameraActivity();
                        if (camAct.isValidContainerNumber(containerNumberView.getText().toString())) {
                            conStatusView.setText(item, false);
                            dialog = createDialog("Damaged");
                            TextView t = dialog.findViewById(R.id.conMsg);
                            t.setText(containerNumberView.getText().toString());
                            dialog.show();
                        } else {
                            txtMessage.setText("Please enter the valid Container number");
                            mToast.show();
                        }

                    } else {
                        conStatusView.setText("");
                        txtMessage.setText("All Fields are required");
                        mToast.show();
                    }
                }
            }
        });
    }


    public void loadSurveyorList()
    {

        LayoutInflater inflater = getLayoutInflater();
        View customToastLayout = inflater.inflate(R.layout.activity_custom_toast, (ViewGroup) findViewById(R.id.custom_toast_container));
        TextView txtMessage = customToastLayout.findViewById(R.id.text);
        Toast mToast = new Toast(getApplicationContext());
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.setView(customToastLayout);

        try {
            if (selectedLocation != null) {

                DBHelper.LoadSurveyors(new MySurveyorsCallback() {
                    @Override
                    public void onCallback(ArrayList<String> arrayList) {
                        surveyors = arrayList;
                        if (surveyors != null) {
                            loadedSurveyors = new String[surveyors.size()];
                            loadedSurveyors = surveyors.toArray(loadedSurveyors);
                            setDropdowns(surveyorView, loadedSurveyors);
                        }
                    }
                }, selectedLocation);


            }
        } catch (Exception ex) {
            txtMessage.setText("Error in Loading Surveyors list");
            mToast.show();
        }

    }

    public Dialog createDialog(String conStatus)
    {
        //Dialog to confirm the Container No
        dialog = new Dialog(UserFormActivity.this);
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
                if (conStatus.equals("Damaged")) {

                    HashMap<String, Object> Activity = getFilledData(conStatus);
                    intent = new Intent(UserFormActivity.this, DamagedContainerActivity.class);
                    intent.putExtra("map", Activity);
                    startActivity(intent);
                } else {
                    saveData();
                }
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

    public void saveData()
    {
        LayoutInflater inflater = getLayoutInflater();
        View customToastLayout = inflater.inflate(R.layout.activity_custom_toast, (ViewGroup) findViewById(R.id.custom_toast_container));
        TextView txtMessage = customToastLayout.findViewById(R.id.text);
        Toast mToast = new Toast(getApplicationContext());
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.setView(customToastLayout);
        //GoogleDriveHelperClass.GenerateLinkForImages();

        Map<String, Object> Activity = getFilledData("damaged");

        DBHelper.SaveDetails(new MySaveCallBack() {
            @Override
            public void onCallbackForSaveData(boolean status) {
                if (status) {
                    Toast.makeText(UserFormActivity.this, "Data saved", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UserFormActivity.this, CameraActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(UserFormActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }

            }
        }, Activity);
    }

    public HashMap<String, Object> getFilledData(String conStatus)
    {

        HashMap<String, Object> Activity = new HashMap<>();
        Activity.put("ContainerNumber", containerNumberView.getText().toString());
        Activity.put("ContainerStatus", conStatusView.getText().toString());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
        Activity.put("CreatedTime", new Timestamp(Instant.now()));
        Activity.put("GateStatus", activityView.getText().toString());
        Activity.put("ImageLink", "https://in.search.yahoo.com/yhs/search?p=transworld%20terminals.com&hspart=fc&hsimp=yhs-2461&type=fc_A7B54195D6A_s58_g_e_d040624_n9998_c999&param1=7&param2=eJwtj8tugzAQRX%2FFy0QKZPzAxmbVUPcDqq4aZQHGIRZPARVVv77jtPLmzJ3j0Uwbmmtxe3%2BlAELncD3dRqy11jlibIEAyQQW7i9HCjMiw9woAWCcMJJ7ZXjDa6OavDaN0sxUjke59RPaYUT8qpCG6Sf0fXXOUiCHPYzNtK9k3AiFFAqCgRQF%2BZbiSKp57v3u6y5s54yrlEty6B7b0J9IHzpPWu%2B66UjcY5kGf6YcB8RH1upeLeH%2FS1x3fd4YF1j98mRNwVpbQsKtUgml9i3JXy4ysTQrgemSW7hE30WZARMJoAYfIIxQJmOpEvnnL7YqWfo%3D");
        Activity.put("Location", locView.getText().toString());
        Activity.put("Size", conSizeView.getText().toString());
        Activity.put("Surveyor", surveyorView.getText().toString());
        Activity.put("UpdatedTime", new Timestamp(Instant.now()));
        Activity.put("ShippingLine", "HAPAG");
        if (!conStatus.equals("Damaged")) {
            Activity.put("Remark", remark.getText().toString());
        }
        return Activity;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 1 || requestCode == 123) & resultCode == Activity.RESULT_OK) {
            if (data != null) {
                if (data.getClipData() != null) {
                    int x = data.getClipData().getItemCount();
                    for (int i = 0; i < x; i++) {
                        uri.add(data.getClipData().getItemAt(i).getUri());
                        adapter.notifyDataSetChanged();
                    }
                } else if (data.getData() != null) {
                    String imageURL = data.getData().getPath();
                    uri.add(Uri.parse(imageURL));
                }
            } else {
                uri.add(cam_uri);
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void setDropdowns(AutoCompleteTextView autoCompleteTextView, String[] values)
    {
        adapterItems = new ArrayAdapter<String>(this, R.layout.list_item, values);
        autoCompleteTextView.setAdapter(adapterItems);
    }

    public void moveToNext(View view)
    {

        LayoutInflater inflater = getLayoutInflater();
        View customToastLayout = inflater.inflate(R.layout.activity_custom_toast, (ViewGroup) findViewById(R.id.custom_toast_container));
        TextView txtMessage = customToastLayout.findViewById(R.id.text);
        Toast mToast = new Toast(getApplicationContext());
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.setView(customToastLayout);

        if (CheckAllFields()) {
            if (recyclerView.getAdapter().getItemCount() > 0) {
                CameraActivity camAct = new CameraActivity();
                if (camAct.isValidContainerNumber(containerNumberView.getText().toString())) {
                    dialog = createDialog("Sound");
                    TextView t = dialog.findViewById(R.id.conMsg);
                    t.setText(containerNumberView.getText().toString());
                    dialog.show();
                } else {
                    txtMessage.setText("Please enter the valid Container number");
                    mToast.show();
                }
            } else {
                txtMessage.setText("Please upload atleast 1 image to continue");
                mToast.show();
            }
        } else {
            txtMessage.setText("All Fields are required");
            mToast.show();
        }

    }

    private boolean CheckAllFields()
    {
        boolean retValue = true;
        if (dateTime.length() == 0) {
            return false;
        }

        if (containerNumberView.getText().toString().isEmpty() || activityView.getText().toString().isEmpty() || locView.getText().toString().isEmpty() || surveyorView.getText().toString().isEmpty() || conTypeView.getText().toString().isEmpty() || conSizeView.getText().toString().isEmpty() || conStatusView.getText().toString().isEmpty())
        {
            return false;
        }
        return retValue;
    }
}
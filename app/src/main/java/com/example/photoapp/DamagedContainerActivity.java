package com.example.photoapp;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.HashMap;
import java.util.Objects;

public class DamagedContainerActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecycleAdapter adapter;
    ImageView galleryIcon,cameraIcon,shareIcon;
    ArrayList<Uri> uri = new ArrayList<Uri>();
    private static  final int Read_Permission = 101;
    EditText remark;
    Uri cam_uri;
    static HashMap<String, Object> Activity;
    private GoogleDriveHelper googleDriveHelper;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_damaged_container);
        session = new Session(getApplicationContext());

        googleDriveHelper = new GoogleDriveHelper(this);
        Intent intent = getIntent();
        Activity = (HashMap<String, Object>)intent.getSerializableExtra("map");
        galleryIcon = findViewById(R.id.FolderIconImageView);
        cameraIcon = findViewById(R.id.cameraIconImageView);
        shareIcon = findViewById(R.id.ShareIconImageView);
        recyclerView = findViewById(R.id.recyclerView_Gallery_Images);
        remark = findViewById(R.id.editTextMultiLine);

        adapter = new RecycleAdapter(uri);
        recyclerView.setLayoutManager(new GridLayoutManager(DamagedContainerActivity.this,2));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new RecycleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                uri.remove(position);
                adapter.notifyItemRemoved(position);
            }
        });

        if(ContextCompat.checkSelfPermission(DamagedContainerActivity.this,android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(DamagedContainerActivity.this,
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

        shareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.setType("image/*");
                startActivity(Intent.createChooser(shareIntent, null));
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        ContentResolver contentResolver = getContentResolver();
        super.onActivityResult(requestCode, resultCode, data);
        if((requestCode==1 || requestCode == 123) & resultCode == android.app.Activity.RESULT_OK){
            if (data != null) {
                if (data.getClipData() != null) {
                    try
                    {
                        int x = data.getClipData().getItemCount();
                        for (int i = 0; i < x; i++)
                        {

                            Bitmap photo = MediaStore.Images.Media.getBitmap(contentResolver, data.getClipData().getItemAt(i).getUri());
                            if(photo.getWidth()>1000 && photo.getHeight()>1000)
                            {
                                uri.add(data.getClipData().getItemAt(i).getUri());
                                adapter.notifyDataSetChanged();
                            }
                            else
                            {
                                Toast.makeText(DamagedContainerActivity.this,"Poor quality! Upload another image!",Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                    catch(Exception e) {
                        Toast.makeText(DamagedContainerActivity.this,"Image Error",Toast.LENGTH_SHORT).show();
                    }
                } else if (data.getData() != null) {
                    String imageURL = data.getData().getPath();
                    uri.add(Uri.parse(imageURL));

                }
            } else {
                try {
                    Bitmap photo = MediaStore.Images.Media.getBitmap(contentResolver, cam_uri);
                    if(photo.getWidth()>1000 && photo.getHeight()>1000)
                    {
                        uri.add(cam_uri);
                        adapter.notifyDataSetChanged();
                    }
                    else
                    {
                        Toast.makeText(DamagedContainerActivity.this,"Poor quality! Upload another image!",Toast.LENGTH_SHORT).show();

                    }
                }
                catch (Exception e)
                {
                    Toast.makeText(DamagedContainerActivity.this,"Image Error",Toast.LENGTH_SHORT).show();

                }
            }
        }
        else if (requestCode == 2 & resultCode == android.app.Activity.RESULT_OK) {
            googleDriveHelper.handleSignInResult(new MySaveCallBack(){
                @Override
                public void onCallbackForSaveData(boolean status)
                {
                    if(status)
                    {
                        try {

                            DateTimeFormatter d = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss a");
                            LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
                            String folderName = Activity.get("Location").toString()+"_"+Activity.get("ContainerNumber").toString()+"_"+d.format(now).toString();
                            googleDriveHelper.createFolder(new CreateFolderCallBack(){
                                @Override
                                public void onCallBackCreateFolder(boolean status,String folderId)
                                {
                                    if(status)
                                    {
                                        Activity.put("ImageLink","https://drive.google.com/drive/folders/"+folderId);
                                        if (uri!=null)
                                        {
                                            for (Uri u:uri)
                                            {

                                                String filePath = getRealPathFromURI(u,DamagedContainerActivity.this);
                                                java.io.File file = new java.io.File(filePath);
                                                googleDriveHelper.uploadFileToDrive(new CreateFolderCallBack() {
                                                    @Override
                                                    public void onCallBackCreateFolder(boolean status,String folderId) {
                                                        boolean a = status;
                                                    }
                                                },file, folderId);
                                            }
                                        }
                                        DBHelper.SaveDetails(new MySaveCallBack() {
                                            @Override
                                            public void onCallbackForSaveData(boolean status) {
                                                if (status) {
                                                    Toast.makeText(DamagedContainerActivity.this, "Data saved", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(DamagedContainerActivity.this, CameraActivity.class);
                                                    startActivity(intent);
                                                } else {
                                                    Toast.makeText(DamagedContainerActivity.this, "Failed to save data", Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        }, Activity);
                                    }
                                    else
                                    {
                                        Toast.makeText(DamagedContainerActivity.this,"Error in creating Folder in Google Drive" , Toast.LENGTH_SHORT).show();
                                    }

                                }
                            },folderName,session.getLoc());


                        }
                        catch(Exception e)
                        {
                            Toast.makeText(DamagedContainerActivity.this,"Error in creating Folder in Google Drive" , Toast.LENGTH_SHORT).show();
                            Log.d("Drive",  e.toString());
                        }
                    }
                    else
                    {
                        Toast.makeText(DamagedContainerActivity.this,"Error in Sign In to the Google Account" , Toast.LENGTH_SHORT).show();

                    }

                }
            },data);


        }
    }

    public String getRealPathFromURI(Uri contentUri, Context a) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;

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
            if(Objects.requireNonNull(recyclerView.getAdapter()).getItemCount()>0)
            {
                Activity.put("Remark",remark.getText().toString());
                googleDriveHelper.signIn(new MySaveCallBack() {
                    @Override
                    public void onCallbackForSaveData(boolean status)
                    {
                        if(status) {

                        }
                        else {
                            Toast.makeText(DamagedContainerActivity.this, "Google Drive Error!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
            else
            {
                txtMessage.setText("Please upload atleast 1 image to continue");
                mToast.show();
            }
        }
        else
        {
            txtMessage.setText("Enter the Damaged container details");
            mToast.show();
        }

    }



    private boolean CheckAllFields() {

        boolean retValue = true;
        if (remark.length() == 0) {

            return false;
        }
        return retValue;
    }
}

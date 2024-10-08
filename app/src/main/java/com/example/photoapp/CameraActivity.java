package com.example.photoapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class CameraActivity extends AppCompatActivity {
    ActivityResultLauncher<Intent> resultLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        registerResult();
    }
    public void captureImage(View view) {
        Intent open_camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(open_camera,123);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            ImageView capturedImageView = findViewById(R.id.cameraImageView);
            capturedImageView.setImageBitmap(photo);

        }
    }

    public void registerResult(){
                resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        try{
                            Uri imageUri = result.getData().getData();
                            ImageView capturedImageView = findViewById(R.id.cameraImageView);
                            capturedImageView.setImageURI(imageUri);
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(CameraActivity.this,"No Image Selected",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }
    public void imageFromGallery(View view) {
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        resultLauncher.launch(intent);
    }

    public void moveToNext(View view) {
        Intent intent = new Intent(CameraActivity.this, UserFormActivity.class);
        startActivity(intent);
    }
}

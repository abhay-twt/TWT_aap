package com.example.photoapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.Objects;

public class CameraActivity extends AppCompatActivity {
    ActivityResultLauncher<Intent> resultLauncher;
    static String ContainerNumber ="";
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
            ContainerNumber = getTextFromImage(photo);
        }
    }

    private String getTextFromImage(Bitmap photo) {
        TextRecognizer recognizer = new TextRecognizer.Builder(this).build();
        String text = null;
        if(!recognizer.isOperational())
        {
            Toast.makeText(CameraActivity.this,"Error Occured",Toast.LENGTH_SHORT).show();
        }
        else {
            Frame frame = new Frame.Builder().setBitmap(photo).build();
            SparseArray<TextBlock> textBlockSparseArray = recognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();
            for(int i=0; i<textBlockSparseArray.size();i++)
            {
                TextBlock textBlock =textBlockSparseArray.valueAt(i);
                stringBuilder.append(textBlock.getValue());
                stringBuilder.append("\n");
            }
            Toast.makeText(CameraActivity.this,stringBuilder.toString(),Toast.LENGTH_SHORT).show();
            text = stringBuilder.toString();
        }
        return text;
    }

    public void registerResult(){
                resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        try{
                            Uri imageUri = result.getData().getData();
                            ContentResolver contentResolver = getContentResolver();
                            ImageView capturedImageView = findViewById(R.id.cameraImageView);
                            assert imageUri != null;
                            Bitmap photo = MediaStore.Images.Media.getBitmap(contentResolver , imageUri);
                            ContainerNumber = getTextFromImage(photo);
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

package com.example.photoapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
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
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CameraActivity extends AppCompatActivity {
    ActivityResultLauncher<Intent> resultLauncher;
    static String ContainerNumber ="";
    boolean flag = false;
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
            flag = true;
            //ContainerNumber = getValidContainerNumbersFromImage(photo).get(0);
        }
    }

    // Function to extract and validate container numbers
    private ArrayList<String> getValidContainerNumbersFromImage(Bitmap photo)
    {
        TextRecognizer recognizer = new TextRecognizer.Builder(this).build();
        ArrayList<String> validContainerNumbers = new ArrayList<>();

        if (!recognizer.isOperational()) {
            Toast.makeText(CameraActivity.this, "OCR Error Occurred", Toast.LENGTH_SHORT).show();
            return validContainerNumbers;
        }

        // Detect text in the image
        Frame frame = new Frame.Builder().setBitmap(photo).build();
        SparseArray<TextBlock> textBlockSparseArray = recognizer.detect(frame);
        StringBuilder stringBuilder = new StringBuilder();

        // Concatenate all detected text into a single string
        for (int i = 0; i < textBlockSparseArray.size(); i++) {
            TextBlock textBlock = textBlockSparseArray.valueAt(i);
            stringBuilder.append(textBlock.getValue().replaceAll("\\s+", ""));
            stringBuilder.append("\n");
        }

        String detectedText = stringBuilder.toString();

        // Regex pattern for container number (4 letters + 6 digits + 1 check digit)
        Pattern pattern = Pattern.compile("[A-Z]{4}\\d{6}\\d");

        // Extract and validate each container number
        Matcher matcher = pattern.matcher(detectedText);
        while (matcher.find()) {
            String containerNumber = matcher.group();
            if (isValidContainerNumber(containerNumber)) {
                validContainerNumbers.add(containerNumber);
            }
        }

        // Display results
        if (!validContainerNumbers.isEmpty()) {
            Toast.makeText(CameraActivity.this, "Valid Containers: " + validContainerNumbers, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(CameraActivity.this, "No valid container numbers found", Toast.LENGTH_SHORT).show();
        }

        return validContainerNumbers;
    }

    private boolean isValidContainerNumber(String container) {
        if (container.length() != 11) return false;

        // Convert letters to numeric values and multiply by positional weights
        int[] weights = {2, 4, 8, 16, 32, 16, 8, 4, 2, 1}; // Weights for each position
        int sum = 0;

        for (int i = 0; i < 10; i++) {
            char ch = container.charAt(i);
            int value;

            if (Character.isLetter(ch)) {
                value = (ch - 'A' + 10); // Convert letter to numeric value (A=10, B=11, ...)
            } else {
                value = Character.getNumericValue(ch); // Use numeric value for digits
            }

            sum += value * weights[i];
        }

        int expectedCheckDigit = sum % 11;
        if (expectedCheckDigit == 10) expectedCheckDigit = 0; // Handle modulo 11 = 10 case

        // Compare calculated check digit with the actual one
        int actualCheckDigit = Character.getNumericValue(container.charAt(10));
        return expectedCheckDigit == actualCheckDigit;
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
                            capturedImageView.setImageURI(imageUri);
                            flag = true;
                            ContainerNumber = getValidContainerNumbersFromImage(photo).isEmpty()?"":getValidContainerNumbersFromImage(photo).get(0);
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

    public void moveToNext(View view)
    {
        ImageView capturedImageView = findViewById(R.id.cameraImageView);

        if(flag == true)
        {
            Intent intent = new Intent(CameraActivity.this, UserFormActivity.class);
            intent.putExtra("cno",ContainerNumber);
            startActivity(intent);
        }
        else
        {
            LayoutInflater inflater = getLayoutInflater();
            View customToastLayout = inflater.inflate(R.layout.activity_custom_toast,(ViewGroup) findViewById(R.id.custom_toast_container));
            TextView txtMessage = customToastLayout.findViewById(R.id.text);
            Toast mToast = new Toast(getApplicationContext());
            mToast.setDuration(Toast.LENGTH_SHORT);
            mToast.setView(customToastLayout);
            txtMessage.setText("Please Select Container Image");
            mToast.show();
        }

    }
}

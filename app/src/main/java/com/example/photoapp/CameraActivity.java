package com.example.photoapp;
import java.util.ArrayList;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresExtension;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import java.util.Arrays;
import java.util.Objects;

public class CameraActivity extends AppCompatActivity {
    ActivityResultLauncher<Intent> galleryResultLauncher,cameraResultLauncher;
    ArrayList<String> validContainerNumbers = new ArrayList<>();
    boolean flag = false;
    Uri cam_uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        registerResult();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


       getMenuInflater().inflate(R.menu.menu,menu);
       return true;
    }


    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


       if(item.getItemId() == R.id.logout)
       {
           LogoutAlert l = new LogoutAlert();
           AlertDialog alert = l.alert(CameraActivity.this,getApplicationContext());
           alert.show();

       }

        return true;
    }

    public void captureImage(View view) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
        cam_uri = getApplicationContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cam_uri);
        cameraResultLauncher.launch(cameraIntent);
    }

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    public void imageFromGallery(View view) {
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        galleryResultLauncher.launch(intent);
    }

    public void registerResult(){
        galleryResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        try{
                            assert result.getData() != null;
                            cam_uri = result.getData().getData();
                            ContentResolver contentResolver = getContentResolver();
                            ImageView capturedImageView = findViewById(R.id.cameraImageView);
                            assert cam_uri != null;
                            Bitmap photo = MediaStore.Images.Media.getBitmap(contentResolver,cam_uri);
                            if(photo.getWidth()>1000 && photo.getHeight()>1000)
                            {
                                capturedImageView.setImageBitmap(photo);
                                flag = true;
                                validContainerNumbers = getValidContainerNumbersFromImage(photo);
                            }
                            else
                            {
                                Toast.makeText(CameraActivity.this,"The Quality of image is poor! Upload another image!",Toast.LENGTH_SHORT).show();

                            }

                        }
                        catch (Exception e)
                        {
                            Toast.makeText(CameraActivity.this,"No Image Selected",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );


        cameraResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    try
                    {
                        if (result.getResultCode() == RESULT_OK) {
                            // There are no request codes
                            ImageView capturedImageView = findViewById(R.id.cameraImageView);
                            ContentResolver contentResolver = getContentResolver();
                            Bitmap photo = MediaStore.Images.Media.getBitmap(contentResolver,cam_uri);
                            if(photo.getWidth()>1000 && photo.getHeight()>1000)
                            {
                                capturedImageView.setImageBitmap(photo);
                                flag = true;
                                validContainerNumbers = getValidContainerNumbersFromImage(photo);
                            }
                            else
                            {
                                Toast.makeText(CameraActivity.this,"Poor quality! Upload another image!",Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(CameraActivity.this,"No Image Selected",Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    // Function to extract and validate container numbers
    private ArrayList<String> getValidContainerNumbersFromImage(Bitmap photo)
    {
        TextRecognizer recognizer = new TextRecognizer.Builder(this).build();
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
            stringBuilder.append(textBlock.getValue().replaceAll("/n", " "));
        }

        String detectedText = stringBuilder.toString();
        validContainerNumbers = ocrTextExtract(detectedText);

        // Display results
        if (!validContainerNumbers.isEmpty()) {
            Toast.makeText(CameraActivity.this, "Valid Containers: " + validContainerNumbers, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(CameraActivity.this, "No valid container numbers found", Toast.LENGTH_SHORT).show();
        }

        return validContainerNumbers;
    }

    private ArrayList<String> ocrTextExtract(String detectedText)
    {
        ArrayList<String> possibleContainerNos = new ArrayList<String>();
        ArrayList<String> validContainerNos = new ArrayList<String>();
        ArrayList<String> ownerCategoryId = new ArrayList<String>();
        ArrayList<String> possibleCheckDigits = new ArrayList<String>();
        ArrayList<String> possibleSerialNumbers = new ArrayList<String>();
        ArrayList<String> possibleSerialCheck = new ArrayList<String>();
        ArrayList<String> possible3digSerialNos = new ArrayList<String>();
        detectedText = detectedText.replace("\n"," ");
        detectedText = detectedText.replace("\"","");
        String[] splitText = detectedText.split(" ");

        Arrays.asList(splitText).forEach(n->
        {
            if(n.length()==4 && n.equals(n.toUpperCase()) && (n.endsWith("U") || n.endsWith("J") || n.endsWith("Z")))
            {
                ownerCategoryId.add(n);
            }

            if(n.length() == 1 && n.chars().allMatch( Character::isDigit ))
            {
                possibleCheckDigits.add(n);
            }

            if(n.length() == 6 && n.chars().allMatch( Character::isDigit ))
            {
                possibleSerialNumbers.add(n);
            }

            if(n.length() == 3 && n.chars().allMatch( Character::isDigit ))
            {
                possible3digSerialNos.add(n);
            }

            if(n.length() == 7 && n.chars().allMatch( Character::isDigit ))
            {
                possibleSerialCheck.add(n);
            }
        });

        // combining 3 digit serial nos to form 6 digit serial nos
        if(!possible3digSerialNos.isEmpty())
        {
            for(int i =0;i<possible3digSerialNos.size()-1;i++)
            {
                for(int j=1;j<possible3digSerialNos.size();j++)
                {
                    possibleSerialNumbers.add(possible3digSerialNos.get(i)+possible3digSerialNos.get(j));
                }
            }
        }

        // Possible container nos  formed by 6 digit serial nos using 3 digit serial nos
        if(!ownerCategoryId.isEmpty() && !possibleCheckDigits.isEmpty() && !possibleSerialNumbers.isEmpty())
        {
            for(String ocId : ownerCategoryId)
            {
                for(String cd : possibleCheckDigits)
                {
                    for(String sn : possibleSerialNumbers)
                    {
                        possibleContainerNos.add(ocId+sn+cd);
                    }
                }
            }
        }

        if(!ownerCategoryId.isEmpty() && !possibleSerialCheck.isEmpty())
        {
            for(String ocId : ownerCategoryId)
            {
                for(String sc :possibleSerialCheck)
                {
                    possibleContainerNos.add(ocId+sc);
                }
            }
        }

        for(String cno : possibleContainerNos)
        {
            if(isValidContainerNumber(cno))
            {
                validContainerNos.add(cno);
            }
        }

        return validContainerNos;

    }

    public boolean isValidContainerNumber(String container) {
        if (container.length() != 11) return false;

        // Convert letters to numeric values and multiply by positional weights
        int[] weights = {1,2, 4, 8, 16, 32, 64, 128, 256, 512}; // Weights for each position
        int sum = 0;
        for (int i = 0; i < 10; i++) {
            char ch = container.charAt(i);
            int value;
            if (Character.isLetter(ch)) {
                value = ch-55;

                if(value % 10 != 0)
                {
                    value = value+(value/10);
                }
                else
                {
                    value = value+(value/10)-1;
                }

            } else {
                value = Character.getNumericValue(ch); // Use numeric value for digits
            }

            sum += value * weights[i];
        }

        int expectedCheckDigit = sum - ((sum / 11)*11);
        if (expectedCheckDigit == 10) expectedCheckDigit = 0; // Handle modulo 11 = 10 case

        // Compare calculated check digit with the actual one
        int actualCheckDigit = Character.getNumericValue(container.charAt(10));
        return expectedCheckDigit == actualCheckDigit;
    }

    public void moveToNext(View view)
    {


        if(flag)
        {
            if(validContainerNumbers.size()>0)
            {
                Intent intent = new Intent(CameraActivity.this, UserFormActivity.class);
                intent.putStringArrayListExtra("cno",validContainerNumbers);
                intent.putExtra("masterImg",cam_uri.toString());
                startActivity(intent);
            }
            else
            {
                Toast.makeText(CameraActivity.this,"No valid Container number! Please upload valid container Image!",Toast.LENGTH_SHORT).show();

            }

        }
        else
        {
            LayoutInflater inflater = getLayoutInflater();
            View customToastLayout = inflater.inflate(R.layout.activity_custom_toast,(ViewGroup) findViewById(R.id.custom_toast_container));
            TextView txtMessage = customToastLayout.findViewById(R.id.text);
            Toast mToast = new Toast(getApplicationContext());
            mToast.setDuration(Toast.LENGTH_SHORT);
            mToast.setView(customToastLayout);
            txtMessage.setText("Please select Container Image");
            mToast.show();
        }

    }
}

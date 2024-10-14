package com.example.photoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    /*one boolean variable to check whether all the text fields
        are filled by the user, properly or not.*/
    boolean isAllFieldsChecked = false;
    EditText userName, passWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Fetching All Fields
        userName = findViewById(R.id.loginUsername);
        passWord = findViewById(R.id.loginPassword);
    }

    public void goToCamera(View view) {
        LayoutInflater inflater = getLayoutInflater();
        View customToastLayout = inflater.inflate(R.layout.activity_custom_toast,(ViewGroup) findViewById(R.id.custom_toast_container));
        TextView txtMessage = customToastLayout.findViewById(R.id.text);
        Toast mToast = new Toast(getApplicationContext());
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setView(customToastLayout);
        isAllFieldsChecked = CheckAllFields();

        if(isAllFieldsChecked)
        {
            Intent intent = new Intent(LoginActivity.this, CameraActivity.class);
            startActivity(intent);
        }
        else
        {
            txtMessage.setText("All Fields are required");
            mToast.show();
        }

    }

    private boolean CheckAllFields() {

        boolean retValue = true;
        LayoutInflater inflater = getLayoutInflater();
        View customToastLayout = inflater.inflate(R.layout.activity_custom_toast,(ViewGroup) findViewById(R.id.custom_toast_container));
        Toast mToast = new Toast(getApplicationContext());
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.setView(customToastLayout);

        if (userName.length() == 0) {

            return false;
        }

        if (passWord.length() == 0) {

            return false;
        }
        return retValue;
    }
}

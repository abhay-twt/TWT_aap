package com.example.photoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    /*one boolean variable to check whether all the text fields
        are filled by the user, properly or not.*/
    boolean isAllFieldsChecked = false;
    EditText userNameField, passWordField;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Fetching All Fields
        userNameField = findViewById(R.id.loginUsername);
        passWordField = findViewById(R.id.loginPassword);
        db = FirebaseFirestore.getInstance();
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
            String userName = userNameField.getText().toString();
            String passWord = passWordField.getText().toString();
            try {
                db.collection("User")
                        .whereEqualTo("UserName", userName)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful())
                                {
                                    QueryDocumentSnapshot document;
                                    if(!task.getResult().isEmpty())
                                    {
                                        if(Objects.equals(task.getResult().getDocuments().get(0).get("Password"), passWord))
                                        {
                                            Intent intent = new Intent(LoginActivity.this, CameraActivity.class);
                                            startActivity(intent);
                                        }
                                        else
                                        {
                                            txtMessage.setText("Invalid Password");
                                            mToast.show();
                                        }
                                    }
                                    else
                                    {
                                        txtMessage.setText("Invalid Username");
                                        mToast.show();
                                    }
                                }
                                else
                                {
                                    txtMessage.setText((CharSequence) task.getException());
                                    mToast.show();
                                }
                            }
                        });
            }
            catch (Exception ex)
            {
                txtMessage.setText("DB Error");
                mToast.show();
            }
        }
        else
        {
            txtMessage.setText("All Fields are required");
            mToast.show();
        }

    }

    private boolean CheckAllFields() {

        boolean retValue = true;
        if (userNameField.length() == 0) {

            return false;
        }
        if (passWordField.length() == 0) {

            return false;
        }
        return retValue;
    }
}

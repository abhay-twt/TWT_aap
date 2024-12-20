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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    /*one boolean variable to check whether all the text fields
        are filled by the user, properly or not.*/
    boolean isAllFieldsChecked = false;
    EditText userNameField, passWordField;
    FirebaseFirestore db;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Fetching All Fields
        userNameField = findViewById(R.id.loginUsername);
        passWordField = findViewById(R.id.loginPassword);
        db = FirebaseFirestore.getInstance();
        session = new Session(getApplicationContext());
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
            HashMap<String, Object> User = new HashMap<>();
            try {
                db.collection("User")
                        .whereEqualTo("EmpId", userName)
                        .whereEqualTo("Status","Active")
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
                                            String id =task.getResult().getDocuments().get(0).getId().toString();
                                            DateTimeFormatter d = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss a");
                                            LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
                                            User.put("LastLoginTime",d.format(now).toString());
                                            User.put("CreatedTime",task.getResult().getDocuments().get(0).get("CreatedTime").toString());
                                            User.put("Email",task.getResult().getDocuments().get(0).get("Email").toString());
                                            User.put("EmpId",task.getResult().getDocuments().get(0).get("EmpId").toString());
                                            User.put("FirstName",task.getResult().getDocuments().get(0).get("FirstName").toString());
                                            User.put("LastName",task.getResult().getDocuments().get(0).get("LastName").toString());
                                            String loc = task.getResult().getDocuments().get(0).get("Location").toString();
                                            User.put("Location",loc);
                                            session.setLoc(loc);
                                            User.put("Password",task.getResult().getDocuments().get(0).get("Password").toString());
                                            User.put("PhoneNumber",task.getResult().getDocuments().get(0).get("PhoneNumber").toString());
                                            User.put("Role",task.getResult().getDocuments().get(0).get("Role").toString());
                                            User.put("Status",task.getResult().getDocuments().get(0).get("Status").toString());
                                            User.put("ConPassword",task.getResult().getDocuments().get(0).get("ConPassword").toString());


                                            DBHelper.updateTime(id,User,new MySaveCallBack() {
                                                @Override
                                                public void onCallbackForSaveData(boolean status)
                                                {
                                                    if(status) {

                                                        Intent intent = new Intent(LoginActivity.this, CameraActivity.class);
                                                        startActivity(intent);
                                                    }
                                                    else {
                                                        Toast.makeText(LoginActivity.this, "Not able to update Login time, Try Again!", Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            });

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

                                    String a = task.getException().toString();
                                    txtMessage.setText( task.getException().toString());
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

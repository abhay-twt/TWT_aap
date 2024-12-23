package com.example.photoapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LogoutAlert extends AppCompatActivity {

    public AlertDialog alert(Activity cameraActivity, Context applicationContext) {
        AlertDialog.Builder builder = new AlertDialog.Builder(cameraActivity);
        builder.setTitle("Confirmation PopUp!").
                setMessage("You sure, that you want to logout?");
        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(applicationContext,
                                MainActivity.class);
                        startActivity(i);
                    }
                });
        builder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }
}

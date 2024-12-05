package com.example.photoapp;

import android.os.AsyncTask;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

public class CreateFolder extends AsyncTask<String,Void,Void> {

    String folderName;
    Drive mDriveService;
    MySaveCallBack mySaveCallBack;

    CreateFolder(MySaveCallBack mySaveCallBack, String folderName, Drive mDriveService) {
        // list all the parameters like in normal class define
        this.folderName = folderName;
        this.mDriveService = mDriveService;
        this.mySaveCallBack = mySaveCallBack;

    }
    @Override
    protected Void doInBackground(String... params)
    {
        File fileMetadata = new File();
        fileMetadata.setName(folderName);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");

        if(mDriveService!=null) {
            try {
                File folder = mDriveService.files().create(fileMetadata)
                        .setFields("id")
                        .execute();
                mySaveCallBack.onCallbackForSaveData(true);

            } catch (Exception e) {
                String a = e.toString();
                mySaveCallBack.onCallbackForSaveData(false);
            }
        }
        return null;
    }
}

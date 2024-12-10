package com.example.photoapp;

import android.os.AsyncTask;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.util.Collections;

public class CreateFolder extends AsyncTask<String,Void,Void> {

    String folderName;
    Drive mDriveService;
    CreateFolderCallBack createFolderCallBack;

    CreateFolder(CreateFolderCallBack createFolderCallBack, String folderName, Drive mDriveService) {
        // list all the parameters like in normal class define
        this.folderName = folderName;
        this.mDriveService = mDriveService;
        this.createFolderCallBack = createFolderCallBack;

    }
    @Override
    protected Void doInBackground(String... params)
    {
        File fileMetadata = new File();
        fileMetadata.setName(folderName);
        fileMetadata.setParents(Collections.singletonList("root"));
        fileMetadata.setMimeType("application/vnd.google-apps.folder");

        if(mDriveService!=null) {
            try {
                File folder = mDriveService.files().create(fileMetadata)
                        .setFields("id")
                        .execute();
                String e = folder.getId();
                createFolderCallBack.onCallBackCreateFolder(true,folder.getId());

            } catch (Exception e) {
                String a = e.toString();
                createFolderCallBack.onCallBackCreateFolder(true,null);
            }
        }
        return null;
    }
}

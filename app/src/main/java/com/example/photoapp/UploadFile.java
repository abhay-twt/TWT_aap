package com.example.photoapp;
import android.os.AsyncTask;
import android.util.Log;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import java.util.Collections;

public class UploadFile extends AsyncTask<String,Void,Void> {

    String folderId;
    Drive mDriveService;
    MySaveCallBack mySaveCallBack;
    java.io.File filePath;

    UploadFile(MySaveCallBack mySaveCallBack, String folderId, Drive mDriveService,java.io.File file) {
        // list all the parameters like in normal class define

        this.mDriveService = mDriveService;
        this.mySaveCallBack = mySaveCallBack;
        this.filePath= file;
        this.folderId = folderId;
    }
    @Override
    protected Void doInBackground(String... strings) {
        File fileMetadata = new File();
        fileMetadata.setName(filePath.getName());
        fileMetadata.setParents(Collections.singletonList("Kolkata_FSCU9791365_2024/12/03 18:41:08 pm"));
        FileContent mediaContent = new FileContent("image/jpeg",filePath);
        File uploadedFile = null;
        try {
            Thread.sleep(10000);
            uploadedFile = mDriveService.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();
            mySaveCallBack.onCallbackForSaveData(true);
        } catch (Exception e) {
            mySaveCallBack.onCallbackForSaveData(false);
        }

        Log.d("Drive", "File uploaded with ID: " + uploadedFile.getId());
        return null;
    }
}

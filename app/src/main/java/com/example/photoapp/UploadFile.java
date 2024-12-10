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
    CreateFolderCallBack createFolderCallBack;
    java.io.File filePath;

    UploadFile(CreateFolderCallBack createFolderCallBack, String folderId, Drive mDriveService, java.io.File file) {
        // list all the parameters like in normal class define

        this.mDriveService = mDriveService;
        this.createFolderCallBack = createFolderCallBack;
        this.filePath= file;
        this.folderId = folderId;
    }
    @Override
    protected Void doInBackground(String... strings) {
        File fileMetadata = new File();
        fileMetadata.setName(filePath.getName());
        fileMetadata.setParents(Collections.singletonList(folderId));
        FileContent mediaContent = new FileContent("image/jpeg",filePath);
        File uploadedFile = null;
        try {
            uploadedFile = mDriveService.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();
            createFolderCallBack.onCallBackCreateFolder(true,folderId);
            Log.d("Drive", "File uploaded with ID: " + uploadedFile.getId());
        } catch (Exception e) {
            Log.d("Drive", "File uploaded with ID: "+e.toString());
            createFolderCallBack.onCallBackCreateFolder(false,null);
        }


        return null;
    }
}

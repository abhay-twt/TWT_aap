package com.example.photoapp;

import android.os.AsyncTask;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.util.Collections;

public class CreateFolder extends AsyncTask<String,Void,Void> {

    String folderName;
    Drive mDriveService;
    CreateFolderCallBack createFolderCallBack;
    String loc;
    CreateFolder(CreateFolderCallBack createFolderCallBack, String folderName, Drive mDriveService, String loc) {
        // list all the parameters like in normal class define
        this.folderName = folderName;
        this.mDriveService = mDriveService;
        this.createFolderCallBack = createFolderCallBack;
        this.loc = loc;

    }
    @Override
    protected Void doInBackground(String... params)
    {
        File fileMetadata = new File();
        fileMetadata.setName(folderName);
        String locFolderId = getfolderId(loc);
        fileMetadata.setParents(Collections.singletonList(locFolderId));
        fileMetadata.setMimeType("application/vnd.google-apps.folder");

        if(mDriveService!=null) {
            try {
                File folder = mDriveService.files().create(fileMetadata)
                        .setFields("id")
                        .execute();
                createFolderCallBack.onCallBackCreateFolder(true,folder.getId());

            } catch (Exception e) {
                String a = e.toString();
                createFolderCallBack.onCallBackCreateFolder(true,null);
            }
        }
        return null;
    }

    private String getfolderId(String loc) {
        String id = null;
        switch (loc) {
            case "Kolkata":
                id = "1wgVmxI1rZq2XRzpk0pNjGzEQ7a1j9KFk";
                break;
            case "Mundra_1":
                id = "1yAviE7SZog5dTPTDqlw_nAAt3Dww9plu";
                break;
            case "Mundra_2":
                id = "1luMaRj-b079lt09euyttIMafifKa8epj";
                break;
            case "Dadri":
                id = "15SX38kE2u9mMk3ABNqNh7KG04_XIMW10";
                break;
            case "Veshvi":
                id = "14GMNw1HsYtzgnaAf9ECY8V-IQ68IkE6w";
                break;
            case "Mundra_Empty_Park":
                id = "1wn-_qh12PX98BM8UDLRMQjsERLq3K_8a";
                break;
            default:


        }
        return id;
    }
}

package com.example.photoapp;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import java.io.File;
import java.util.Collections;
import java.util.List;
import com.google.api.services.drive.DriveScopes;

public class GoogleDriveHelper {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private static final String APPLICATION_NAME = "Google Drive Quickstart";

    private static final String CLIENT_ID = "161029530023-bs2f0all5min404rmqhkiajvunjs345p.apps.googleusercontent.com";

    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private static final String SCOPES2 = "https://www.googleapis.com/auth/drive";
    private static final List<String> SCOPES =
            Collections.singletonList(DriveScopes.DRIVE_METADATA);
    static final int REQUEST_CODE_SIGN_IN = 2;

    private GoogleSignInClient mGoogleSignInClient;
    private static Drive mDriveService;
    private Activity mActivity;


    public GoogleDriveHelper(Activity activity) {
        this.mActivity = activity;

        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(SCOPES2))
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(mActivity, signInOptions);


    }



    public void signIn(MySaveCallBack mySaveCallBack) {
        try
        {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            mActivity.startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
            mySaveCallBack.onCallbackForSaveData(true);
        }
        catch (Exception e)
        {
            mySaveCallBack.onCallbackForSaveData(false);
        }

    }

    public void handleSignInResult(MySaveCallBack mySaveCallBack, Intent data) {
        GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult();
        if (account != null) {
            GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(mActivity, Collections.singleton(SCOPES2));
            credential.setSelectedAccount(account.getAccount());
            Drive.Builder builder = new Drive.Builder(new NetHttpTransport(), new GsonFactory(), credential).setApplicationName("Photoapp");
            mDriveService = builder.build();
            mySaveCallBack.onCallbackForSaveData(true);
        }
        else
        {
            mySaveCallBack.onCallbackForSaveData(false);
        }
    }

    public void createFolder(CreateFolderCallBack createFolderCallBack, String folderName)
    {
        try
        {
            new CreateFolder(new CreateFolderCallBack(){
                @Override
                public void onCallBackCreateFolder(boolean status,String folderId) {
                    createFolderCallBack.onCallBackCreateFolder(status,folderId);
                }
            },folderName,mDriveService).execute();
        }
        catch (Exception e)
        {
            createFolderCallBack.onCallBackCreateFolder(false,null);
        }


    }

    public void uploadFileToDrive(CreateFolderCallBack createFolderCallBack, File filePath, String folderId) {
        try {

            new UploadFile(new CreateFolderCallBack(){
                @Override
                public void onCallBackCreateFolder(boolean status,String folderId) {
                    createFolderCallBack.onCallBackCreateFolder(status,folderId);
                }
            },folderId,mDriveService,filePath).execute();
        } catch (Exception e) {
            Log.e("Drive", "Error uploading file", e);
            createFolderCallBack.onCallBackCreateFolder(false,folderId);
        }
    }
}
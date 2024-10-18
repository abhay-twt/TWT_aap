package com.example.photoapp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DamagedContainerActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecycleAdapter adapter;
    ImageView icon;
    ArrayList<Uri> uri = new ArrayList<Uri>();
    private static  final int Read_Permission = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_damaged_container);

        icon = findViewById(R.id.FolderIconImageView);
        recyclerView = findViewById(R.id.recyclerView_Gallery_Images);

        adapter = new RecycleAdapter(uri);
        recyclerView.setLayoutManager(new GridLayoutManager(DamagedContainerActivity.this,3));
        recyclerView.setAdapter(adapter);

        if(ContextCompat.checkSelfPermission(DamagedContainerActivity.this,android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(DamagedContainerActivity.this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    Read_Permission);
        }

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
                {
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                }
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 & resultCode == Activity.RESULT_OK){
            if(data.getClipData()!=null)
            {
                int x= data.getClipData().getItemCount();
                for(int i=0;i<x;i++){
                    uri.add(data.getClipData().getItemAt(i).getUri());
                }
                adapter.notifyDataSetChanged();
            }
            if(data.getData()!=null){
                String imageURL = data.getData().getPath();
                uri.add(Uri.parse(imageURL));
            }
        }
    }

    public void moveToNext(View view) {
        Intent intent = new Intent(DamagedContainerActivity.this, MainActivity.class);
        startActivity(intent);
    }


}

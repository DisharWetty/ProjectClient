package com.example.projectclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_uploading;
    Button btn_downloading;
    Button btn_deleting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_uploading = findViewById(R.id.btn_uploading);
        btn_downloading = findViewById(R.id.btn_downloading);
        btn_deleting = findViewById(R.id.btn_deleting);

        btn_uploading.setOnClickListener(this);
        btn_downloading.setOnClickListener(this);
        btn_deleting.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case (R.id.btn_uploading):
                Intent intentUploading = new Intent(this, UploadingActivity.class);
                startActivity(intentUploading);
            break;
            case (R.id.btn_downloading):
                Intent intentDownloading = new Intent(this, DownloadingActivity.class);
                startActivity(intentDownloading);
                break;
            case (R.id.btn_deleting):
                Intent intentDeleting = new Intent(this, DeletingActivity.class);
                startActivity(intentDeleting);
                break;
            default:
                break;
        }
    }

}

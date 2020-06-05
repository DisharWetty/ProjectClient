package com.example.projectclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadingActivity extends AppCompatActivity implements View.OnClickListener{

    Button btn_download;
    EditText txt_fileName;

    String SERVER_URL = "https://192.168.0.6:8080/files/download";

    byte[] data = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloading);

        btn_download = findViewById(R.id.btn_download);
        txt_fileName = findViewById(R.id.txt_fileName);

        btn_download.setOnClickListener(this);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},101);
                return;
            }
        }

    }

    @Override
    public void onClick(View v) {

        if (txt_fileName.getText().toString().equals(""))
            return;

        final String fileName = txt_fileName.getText().toString();

        String fileExtension  = fileName.substring(fileName.lastIndexOf(".")+1);

        //

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                String params = "fileName=" + fileName;

                InputStream is = null;

                try {
                    URL url = new URL(SERVER_URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    conn.setRequestProperty("Content-Length", "" + Integer.toString(params.getBytes().length));
                    OutputStream os = conn.getOutputStream();
                    data = params.getBytes("UTF-8");
                    os.write(data);
                    data = null;

                    conn.connect();
                    int responseCode= conn.getResponseCode();

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    is = conn.getInputStream();

                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        baos.write(buffer, 0, bytesRead);
                    }
                    data = baos.toByteArray();
                } catch (Exception e) {

                    String analizeEx = e.toString();
                    Toast.makeText(DownloadingActivity.this, analizeEx, Toast.LENGTH_LONG).show();

                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (Exception ex) {

                    }
                }
            }
        });

        t.start();



        String appPath = getApplicationInfo().dataDir;

        try
        {
            String path = appPath + "/" + fileName;

            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream stream = new FileOutputStream(path);
            stream.write(data);
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }

        //
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 101 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
            //
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},101);
            }
        }
    }
}

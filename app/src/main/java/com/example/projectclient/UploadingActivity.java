package com.example.projectclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class UploadingActivity extends AppCompatActivity  implements View.OnClickListener{

    String SERVER_URL = "https://192.168.0.6:8080/files/upload";

    Button btn_chooseFile;
    Button btn_uploadFile;
    TextView txt_filePath;

    File file = null;
    String filePath = null;

    ProgressDialog progress;

    private static  final int CHOOSE_FILE_REQUESTCODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploading);

        btn_chooseFile = findViewById(R.id.btn_chooseFile);
        btn_uploadFile = findViewById(R.id.btn_uploadFile);
        txt_filePath = findViewById(R.id.txt_filePath);

        btn_chooseFile.setOnClickListener(this);
        btn_uploadFile.setOnClickListener(this);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
                return;
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 100 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
            //
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case (R.id.btn_chooseFile):

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath());
                intent.setType("*/*");
                intent.setDataAndType(uri, "text/csv");
                startActivityForResult(intent, CHOOSE_FILE_REQUESTCODE);

                break;
            case (R.id.btn_uploadFile):

                if (filePath != null) {
//                progress = new ProgressDialog(UploadingActivity.this);
//                progress.setTitle("Uploading");
//                progress.setMessage("Please wait...");
//                progress.show();
                //

                    Thread t = new Thread(new Runnable() {
                @Override
                public void run() {

                    String content_type  = getMimeType(filePath);

                    String file_path = file.getAbsolutePath();
                    OkHttpClient client = new OkHttpClient();
                    RequestBody file_body = RequestBody.create(MediaType.parse(content_type),file);

                    RequestBody request_body = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("type",content_type)
                            .addFormDataPart("file",file_path.substring(file_path.lastIndexOf("/")+1), file_body)
                            .build();

                    Request request = new Request.Builder()
                            .url(SERVER_URL)
                            .post(request_body)
                            .build();

                    try {
                        Response response = client.newCall(request).execute();

                        if(!response.isSuccessful()){
                            throw new IOException("Error : "+response);
                        }

//                                progress.dismiss();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            t.start();

            //
        }

        break;
        default:
        break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==CHOOSE_FILE_REQUESTCODE){
            if(resultCode==RESULT_OK){

                file = new File(data.getDataString());

                filePath = data.getDataString();
                txt_filePath.setText(filePath);
            }
            else{
                txt_filePath.setText("File was not chosen!");
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private String getMimeType(String path) {

        String extension = MimeTypeMap.getFileExtensionFromUrl(path);

        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

}
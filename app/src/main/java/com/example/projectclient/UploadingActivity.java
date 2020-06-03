package com.example.projectclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadingActivity extends AppCompatActivity  implements View.OnClickListener{

    Button btn_chooseFile;
    Button btn_uploadFile;
    TextView txt_filePath;

    String filePath = null;

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
                // TODO: Upload file!

                //HttpPost httppost = new HttpPost("http://192.168.0.6/files");

                if (filePath != null) {
                    SendRequest sendRequest = new SendRequest();
                    sendRequest.execute("https://192.168.0.6/files", "file", filePath);
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



        private class SendRequest extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String[] params) {
            String responseFromServer = null;
            try
            {
                String url = params[0];
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Accept-Language", "en-US,en,q=0.5");
                String urlParameters = params[1] + "=" + params[2];
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(wr, "UTF-8"));
                writer.write(urlParameters);
                writer.close();
                wr.close();

                // getting information from response stream
                int responseCode = con.getResponseCode();
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = reader.readLine()) != null)
                {
                    response.append(inputLine);
                }
                reader.close();
                responseFromServer = response.toString();
            }
            catch (Exception ex)
            {
                return ex.getMessage();
            }
            return responseFromServer;
        }

        @Override
        protected void onPostExecute(String message)
        {
            Toast.makeText(UploadingActivity.this, message, Toast.LENGTH_LONG).show();
        }
    }
}
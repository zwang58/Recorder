package com.example.croft.recorder;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class WebActivity extends AppCompatActivity {

    static final public String WEBPAGE = "https://cnn.com";
    WebView web;
    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder;
    public static final int RequestPermissionCode = 1;
    MediaPlayer mediaPlayer;
    int fileIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        web = findViewById(R.id.web);
        web.setWebViewClient(new WebViewClient());
        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        web.addJavascriptInterface(new JavaScriptInterface(this), "Android");
    }

    public class JavaScriptInterface {
        Context mContext; // Having the context is useful for lots of things,
        // like accessing preferences.

        /** Instantiate the interface and set the context */
        JavaScriptInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void record(){

            if(checkPermission()) {

                // the path we'll be saving the file to. Notice it is from the external storage
                // directory
                AudioSavePathInDevice =
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileIndex + ".3gp";
                MediaRecorderReady();

                try {
                    // recording starts
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Toast.makeText(mContext, "Recording started",
                        Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(WebActivity.this, new
                        String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
            }
        }

        @JavascriptInterface
        public void stop(){
            mediaRecorder.stop();
            try {
                // Reading a file that already exists
                File f = new File(getFilesDir(), "file.ser");
                FileOutputStream fo = new FileOutputStream(f);
                ObjectOutputStream o = new ObjectOutputStream(fo);
                o.writeObject(fileIndex);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(mContext, "Saving Recording #" + fileIndex, Toast.LENGTH_SHORT).show();
            fileIndex++;
        }

        @JavascriptInterface
        public void play(){
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(AudioSavePathInDevice);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.start();// play the audio
            Toast.makeText(mContext, "Playing", Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void stoprec(){
            if(mediaPlayer != null){
                mediaPlayer.stop(); // stop audio
                mediaPlayer.release(); // free up memory
                MediaRecorderReady();
            }
            Toast.makeText(mContext, "Stopping Playback", Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void exit(){
            Intent intent = new Intent(mContext, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

    }

    public void MediaRecorderReady(){
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    @Override
    protected void onResume() {
        super.onResume();
        web.loadUrl(WEBPAGE);
        try {
            // Reading a file that already exists
            File f = new File(getFilesDir(), "file.ser");
            FileInputStream fi = new FileInputStream(f);
            ObjectInputStream o = new ObjectInputStream(fi);

            try {
                fileIndex = (int) o.readObject() + 1;
            } catch (ClassNotFoundException c) {
                c.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
}

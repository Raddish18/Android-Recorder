package com.example.genrefy;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 1000;
    Button recordButton, playButton, stopButton;
    String pathSave ="";
    MediaPlayer mp;
    MediaRecorder rc;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(!checkPermissionFromDevice())
            requestPermissions();

        recordButton = (Button)findViewById(R.id.recordButton);
        playButton = (Button)findViewById(R.id.play);
        stopButton = (Button)findViewById(R.id.stop);


           recordButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {

                   if(checkPermissionFromDevice()){


                   pathSave = Environment.getExternalStorageDirectory()
                           .getAbsolutePath()+"/"
                           + UUID.randomUUID().toString()+"audio_rec.3pg";
                   setupRecord();
                   try {
                       rc.prepare();
                       rc.start();
                   }
                   catch (IOException e){
                       e.printStackTrace();
                   }
                   recordButton.setEnabled(false);
                   playButton.setEnabled(false);
                   stopButton.setEnabled(false);

                   handler.postDelayed(new Runnable() {
                       @Override
                       public void run() {
                           //Do something after 10s
                           rc.stop();
                           //recordButton.setEnabled(true);
                           playButton.setEnabled(true);
                       }
                   }, 10000);
                   }
                   else{
                       requestPermissions();
                   }

               }

           });

           playButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   stopButton.setEnabled(true);
                   recordButton.setEnabled(false);

                   mp = new MediaPlayer();
                   try {
                       mp.setDataSource(pathSave);
                       mp.prepare();
                   }
                   catch (IOException e) {
                   e.printStackTrace();
                   }
                   mp.start();

               }
           });

           stopButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   stopButton.setEnabled(false);
                   recordButton.setEnabled(true);
                   playButton.setEnabled(true);

                   if(mp != null) {
                       mp.stop();
                       mp.release();
                       setupRecord();
                   }
               }
           });







        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void setupRecord() {
        rc = new MediaRecorder();
        rc.setAudioSource(MediaRecorder.AudioSource.MIC);
        rc.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        rc.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        rc.setOutputFile(pathSave);

    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case REQUEST_PERMISSION_CODE:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Enable Permissions in App Settings", Toast.LENGTH_SHORT).show();
                }
            }
        }

    private boolean checkPermissionFromDevice() {
        int ext_storage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_permission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return ext_storage == PackageManager.PERMISSION_GRANTED && record_permission == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

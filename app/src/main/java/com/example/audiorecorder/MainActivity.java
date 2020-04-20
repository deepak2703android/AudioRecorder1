package com.example.audiorecorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.dnkilic.waveform.WaveView;

import java.io.IOException;
import java.util.Random;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class MainActivity extends AppCompatActivity {

    Button record,stoprecord,play,pause,stop;
    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder ;
    Random random ;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    public static final int RequestPermissionCode = 1;
    MediaPlayer mediaPlayer ;
    int i = 0;
    int length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        record=findViewById(R.id.record);
        stoprecord=findViewById(R.id.stop);
        play=findViewById(R.id.play);
        pause=findViewById(R.id.pause);
        stop=findViewById(R.id.stoppl);

        stoprecord.setEnabled(false);
        play.setEnabled(false);
        pause.setEnabled(false);
        stop.setEnabled(false);

        random = new Random();

        record.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(checkPermission()){
                    stop.setBackgroundColor(Color.TRANSPARENT);
                    record.setBackgroundColor(getResources().getColor(R.color.flamingo));
                    AudioSavePathInDevice = Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + "/" + CreateRandomAudioFileName(5) + "AudioRecording.mp3";
                    MediaRecorderReady();



                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IllegalStateException | IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    record.setEnabled(false);
                    stoprecord.setEnabled(true);

                    Toast.makeText(MainActivity.this, "Recording started", Toast.LENGTH_LONG).show();
                }
                else {

                    requestPermission();

                }

                }


        });


        stoprecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaRecorder.stop();
                record.setBackgroundColor(Color.TRANSPARENT);
                stoprecord.setBackgroundColor(getResources().getColor(R.color.flamingo));
                play.setEnabled(true);
                record.setEnabled(false);
                stoprecord.setEnabled(false);
                stop.setEnabled(false);
                pause.setEnabled(false);
                Toast.makeText(MainActivity.this, "Recording Completed", Toast.LENGTH_LONG).show();

            }
        });


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record.setEnabled(false);
                stoprecord.setEnabled(false);
                stop.setEnabled(true);
                pause.setEnabled(true);
                stoprecord.setBackgroundColor(Color.TRANSPARENT);
                play.setBackgroundColor(getResources().getColor(R.color.flamingo));
                mediaPlayer = new MediaPlayer();
                i=0;

                try {
                    mediaPlayer.setDataSource(AudioSavePathInDevice);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
                pause.setBackgroundColor(Color.TRANSPARENT);
                Toast.makeText(MainActivity.this, "Recording Playing", Toast.LENGTH_LONG).show();
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record.setEnabled(false);
                stoprecord.setEnabled(false);
                stop.setEnabled(true);
                play.setEnabled(true);
                play.setBackgroundColor(Color.TRANSPARENT);


                if (i == 0) {
                    mediaPlayer.pause();
                     length = mediaPlayer.getCurrentPosition();
                    pause.setBackgroundColor(getResources().getColor(R.color.flamingo));
                    Toast.makeText(MainActivity.this, "Recording paused", Toast.LENGTH_LONG).show();
                    i++;
                }
                else if (i == 1) {
                    mediaPlayer.seekTo(length);
                    mediaPlayer.start();
                    pause.setBackgroundColor(Color.TRANSPARENT);

                    Toast.makeText(MainActivity.this, "Recording Resumed", Toast.LENGTH_LONG).show();
                    i = 0;
                }



            }
        });


stop.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        stoprecord.setEnabled(false);
        record.setEnabled(true);
        stop.setEnabled(false);
        play.setEnabled(true);
        pause.setEnabled(false);
        play.setBackgroundColor(Color.TRANSPARENT);
        stop.setBackgroundColor(getResources().getColor(R.color.flamingo));


        if(mediaPlayer != null){

            mediaPlayer.stop();
            mediaPlayer.release();

            MediaRecorderReady();
            pause.setBackgroundColor(Color.TRANSPARENT);
        }
    }
});

    }



    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {

                    boolean StoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {

                        Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();

                    }
                }

                break;
        }
    }

    private void MediaRecorderReady() {

        mediaRecorder=new MediaRecorder();

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    private String CreateRandomAudioFileName(int j) {

        StringBuilder stringBuilder = new StringBuilder( j );

        int k = 0 ;
        while(k < j ) {
            stringBuilder.append(RandomAudioFileName.charAt(random.nextInt(RandomAudioFileName.length())));
            k++ ;
        }
        return stringBuilder.toString();
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

}

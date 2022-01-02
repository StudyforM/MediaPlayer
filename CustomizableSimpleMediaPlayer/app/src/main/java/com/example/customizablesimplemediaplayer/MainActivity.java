package com.example.customizablesimplemediaplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // Permission denied
    private static final String PATH = "/storage/self/primary/DCIM/Camera/XAVCS_4K.MP4";
    private static final String TAG = "CustomizableSimpleMediaplayer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Copy & Paste
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        // https://developer.android.com/reference/android/media/MediaExtractor
        MediaExtractor extractor = new MediaExtractor();
        try {
            Log.d(TAG, "Calling setDataSource");
            extractor.setDataSource(PATH);
        } catch (IOException e) {
            Log.d(TAG, "In setDataSource, raised exception");
            e.printStackTrace();
        }

        // format[0] = video, format[1] = audio
        int numTracks = extractor.getTrackCount();

        for (int i = 0; i < numTracks; i++) {
            MediaFormat format = extractor.getTrackFormat(i);
            Log.d(TAG, "format["+ i +"]" + " = " + format);
        }
        Log.d(TAG, "setDataSource is end");
    }
}
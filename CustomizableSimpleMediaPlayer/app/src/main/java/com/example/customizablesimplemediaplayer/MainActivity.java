package com.example.customizablesimplemediaplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // Permission denied
//    private static final String PATH = "/storage/self/primary/DCIM/Camera/XAVCS_4K.MP4";
    private static final String PATH = "/storage/self/primary/DCIM/Camera/PXL_20201114_014253759.mp4";
    private static final String TAG = "CSMPlayer";
    private static final int INPUT_BUFFER_SIZE = 500000;
    int mVideoSampleSize = 0;
    int mAudioSampleSize = 0;
    int mOtherSampleSize = 0;
    int mVideoIndex = -1;
    int mAudioIndex = -1;
    int mOtherIndex = -1;
    String mVideoFormat = "";
    String mAudioFormat = "";

    MediaExtractor mExtractor = new MediaExtractor();
    MediaCodec mVideoCodec = null;
    MediaCodec mAudioCodec = null;


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

        Log.d(TAG, "Extracting ----->");
        try {
            Log.d(TAG, "setDataSource -->");
            mExtractor.setDataSource(PATH);
            Log.d(TAG, "setDataSource <--");
        } catch (IOException e) {
            Log.d(TAG, "In setDataSource, raised exception");
            e.printStackTrace();
        }

        // TODO:More than 2tracks.
        // format[0] = video, format[1] = audio or format[0] = audio, format[1] = video
        Log.d(TAG, "selectTrack -->");
        int numTracks = mExtractor.getTrackCount();
        for (int i = 0; i < numTracks; i++) {
            MediaFormat format = mExtractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("video/")) {
                mExtractor.selectTrack(i);
                mVideoIndex = i;
                mVideoFormat = mime;
                try {
                    mVideoCodec = MediaCodec.createByCodecName(mVideoFormat);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (mime.startsWith("audio/")) {
                mExtractor.selectTrack(i);
                mAudioIndex = i;
                mAudioFormat = mime;
                try {
                    mAudioCodec = MediaCodec.createByCodecName(mAudioFormat);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                mExtractor.selectTrack(i);
                mOtherIndex = i;
            }
            Log.d(TAG, "format[" + i + "]" + " = " + format);
        }

        Log.d(TAG, "selectTrack <-- Video=" + mVideoFormat + " Audio=" + mAudioFormat);
        Log.d(TAG, "readSampleData -->");
        ByteBuffer inputBuffer = ByteBuffer.allocate(INPUT_BUFFER_SIZE);
        while (true) {
            int sampleSize = 0;
            sampleSize = mExtractor.readSampleData(inputBuffer, 0);
            if (sampleSize < 0) {
                Log.d(TAG, "reach EOS, mVideoSampleSize= " + mVideoSampleSize +
                        "|mAudioSampleSize= " + mAudioSampleSize + "|mOtherSampleSize= " + mOtherSampleSize);
                break;
            }
            if (mExtractor.getSampleTrackIndex() == mVideoIndex) {
                mVideoSampleSize += sampleSize;
            } else if (mExtractor.getSampleTrackIndex() == mAudioIndex) {
                mAudioSampleSize += sampleSize;
            } else {
                mOtherSampleSize += sampleSize;
            }
            mExtractor.advance();
        }
        Log.d(TAG, "readSampleData -->");
//        mExtractor.release();
//        mExtractor = null;
        Log.d(TAG, "Extracting <-----");

        Log.d(TAG, "Decode ----->");
        Log.d(TAG, "Decode <-----");
    }

    // TODO: keep samples to readSampleData()
    public class SampleHolder {
        SampleHolder() {

        }
    }

}
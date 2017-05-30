package com.example.user.lovemessages;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Created by User on 27/02/2017.
 */

public class BackgroundSoundService extends Service {
    private static final String TAG = null;
    MediaPlayer player = new MediaPlayer();

    public IBinder onBind(Intent arg0) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setLooping(true); // Set looping
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String music = intent.getStringExtra("music");
            playMusic(music);
        }

        return 1;
    }

    public void onStart(Intent intent, int startId) {
        // TO DO
    }

    public IBinder onUnBind(Intent arg0) {
        // TO DO Auto-generated method
        return null;
    }

    public void onStop() {

    }

    public void onPause() {

    }

    @Override
    public void onDestroy() {
        stopMusic();
    }

    @Override
    public void onLowMemory() {

    }

    public void playMusic(String url) {
        try {
            player.setDataSource(url);
            player.prepare();
            player.start();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Lỗi", Toast.LENGTH_LONG).show();
        }
    }

    public void stopMusic() {
        player.stop();
    }
}

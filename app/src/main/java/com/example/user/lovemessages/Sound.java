package com.example.user.lovemessages;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import java.io.IOException;


public class Sound {

    private static Sound instance = null;

    private static MediaPlayer mMediaPlayer = null;

    public static Sound getInstance(){
        if(instance == null){
            instance = new Sound();
        }
        return instance;
    }



    public void playSoundFromAssets(Context context, String filename) {
        if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
            return;
        }

        if(mMediaPlayer != null){
            mMediaPlayer.release();
        }

        try {
            mMediaPlayer = new MediaPlayer();
            AssetFileDescriptor assetFileDescriptor = context.getAssets().openFd(filename);
            mMediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
            assetFileDescriptor.close();
            mMediaPlayer.prepare();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void playSoundFromUrl(String url) {
        if(mMediaPlayer != null){
            mMediaPlayer.release();
        }

        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.prepare();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

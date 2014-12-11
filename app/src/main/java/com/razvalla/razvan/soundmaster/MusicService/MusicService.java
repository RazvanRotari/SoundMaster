package com.razvalla.razvan.soundmaster.MusicService;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import com.razvalla.razvan.soundmaster.Model.SongInfo;

import java.io.IOException;
import java.net.URI;

/**
 * Created by Razvan on 12/9/2014.
 */
public class MusicService extends Service implements MediaPlayer.OnCompletionListener {
    private final IBinder mBinder = new MusicBinder();

    private SongInfo mCurrentSong;
    private PlaybackManager playbackManager;

    public class MusicBinder extends Binder {
        public MusicService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MusicService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        playbackManager = new PlaybackManager(getApplicationContext());
        String path = getSong();
//        playbackManager.playSong(path);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    public boolean playSong(String path) {
        return playbackManager.playSong(path);
    }

    public boolean playTest() {
        return playbackManager.playSong(getSong());
    }

    public void stop() {
        playbackManager.stop();
    }

    public void pause() {
        playbackManager.pause();
    }


    private String getSong() {
        Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null);
        if (cursor.getCount() == 0) {
            Log.e("MusicService", "No results");
        }
        cursor.moveToLast();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
        return path;
    }
}

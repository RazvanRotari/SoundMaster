package com.razvalla.razvan.soundmaster.MusicService;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;

import com.google.gson.Gson;
import com.razvalla.razvan.soundmaster.Model.SongInfo;

import java.util.ArrayList;
import java.util.Arrays;


public class MusicService extends Service implements PlaybackManager.OnSongCompleted {
    private final IBinder mBinder = new MusicBinder();
    private final String SHARED_PREFS_FILE = "MusicMaster";
    private final String QUEUE_KEY = "QueueKey";
    private PlaybackManager playbackManager;
    private QueueManager queueManager;

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
        playbackManager.onSongCompleted = this;
        queueManager = new QueueManager();
        loadQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        saveQueue();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    //OnSongCompleted
    public void onSongCompled(PlaybackManager playbackManager) {
        SongInfo songInfo = queueManager.nextSong();
        if (songInfo == null) return;
        playbackManager.playSong(songInfo);
    }

    //Playback
    public boolean playSong(String path) {
        return playbackManager.playSong(path);
    }
    public boolean playSong(SongInfo songInfo) {
        return playbackManager.playSong(songInfo);
    }

    public void stop() {
        playbackManager.stop();
    }

    public void pause() {
        playbackManager.pause();
    }


    //Queue
    public void playNow(SongInfo songInfo) {
        queueManager.playNow(songInfo);
        playSong(songInfo);
        saveQueue();
    }

    public void playNext(SongInfo songInfo) {
        queueManager.playNext(songInfo);
        saveQueue();
    }

    public void addToQueue(SongInfo songInfo) {
        queueManager.addToQueue(songInfo);
        saveQueue();
    }

    public void playAtIndex(int position) {
        queueManager.currentIndex = position;
        SongInfo songInfo = queueManager.getCurrentSong();
        playNow(songInfo);
    }

    public QueueManager getQueueManager() {
        return queueManager;
    }

    void saveQueue() {
        Gson gson = new Gson();
        String value = gson.toJson(queueManager.queue);
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(QUEUE_KEY, value);
        editor.commit();
    }

    void loadQueue() {
        try {
            tryLoadQueue();
        } catch (Exception e) {
            e.printStackTrace();
            clearQueue();
            return;
        }
    }

    void tryLoadQueue() {
        Gson gson = new Gson();
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        String value = prefs.getString(QUEUE_KEY, "");
        if (value.equals("")) {
            return;
        }
        SongInfo[] songs = gson.fromJson(value,SongInfo[].class);
        queueManager.queue = new ArrayList<SongInfo>(Arrays.asList(songs));
    }

    void clearQueue() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(QUEUE_KEY);
        editor.commit();

    }
}

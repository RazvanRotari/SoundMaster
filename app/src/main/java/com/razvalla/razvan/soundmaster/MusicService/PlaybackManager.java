package com.razvalla.razvan.soundmaster.MusicService;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import com.razvalla.razvan.soundmaster.Model.SongInfo;

import java.io.IOException;

/**
 * Created by Razvan on 12/10/2014.
 */
public class PlaybackManager {
    private MediaPlayer mMediaPlayer;
    private Context mApplicationContext;

    public PlaybackManager(Context applicationContext) {
        mApplicationContext = applicationContext;
    }

    public boolean playSong(SongInfo songInfo) {
        return playSong(songInfo.getSongPath().getPath());
    }

    public boolean playSong(String path) {
        MediaPlayer mediaPlayer = getMediaPlayerWithSongPath(path);
        if (mediaPlayer == null) {
            return false;
        }
        mediaPlayer.start();
        return true;
    }
    public void stop() {
        MediaPlayer mediaPlayer = getMediaPlayer();
        mediaPlayer.stop();
        mediaPlayer.reset();
    }

    public void pause() {
        MediaPlayer mediaPlayer = getMediaPlayer();
        mediaPlayer.pause();
    }

    //PRIVATE
    private MediaPlayer getMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }
        return mMediaPlayer;
    }
    private MediaPlayer getMediaPlayerWithSongPath(String songPath) {
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(mApplicationContext, Uri.parse(songPath));
            return mMediaPlayer;
        }
        mMediaPlayer.stop();
        try {
            mMediaPlayer.setDataSource(songPath);
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return mMediaPlayer;
    }
    private MediaPlayer getMediaPlayerForSongInfo(SongInfo songInfo) {
        return getMediaPlayerWithSongPath(songInfo.getSongPath().toString());
    }
}
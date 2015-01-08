package com.razvalla.razvan.soundmaster.Fragments;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.razvalla.razvan.soundmaster.Model.SongInfo;
import com.razvalla.razvan.soundmaster.MusicService.MusicService;
import com.razvalla.razvan.soundmaster.MusicService.QueueManager;
import com.razvalla.razvan.soundmaster.R;

/**
 * Created by Razvan on 12/11/2014.
 */
public class CurrentPlaylistFragment extends ListFragment {
    MusicService musicService;
    QueueManager queueManager;

    @Override
    public void onStart() {
        super.onStart();
        loadData();
    }

    public void onStop() {
        super.onStop();
    }

    private void loadData() {
        if (queueManager == null) return;
        SongsAdapter adapter = new SongsAdapter(getActivity().getApplicationContext(), queueManager);

        setListAdapter(adapter);
        setListShownNoAnimation(true);
    }

    public void onListItemClick(ListView listView, View view, int position, long id) {
        SongInfo songInfo = (SongInfo) getListAdapter().getItem(position);
        musicService.playSong(songInfo.songPath);
    }

    //Setters & Getters
    public void setMusicService(MusicService musicService) {
        this.musicService = musicService;
        this.queueManager = musicService.getQueueManager();
        loadData();
    }
}

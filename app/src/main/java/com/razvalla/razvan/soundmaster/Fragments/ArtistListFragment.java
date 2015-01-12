package com.razvalla.razvan.soundmaster.Fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.razvalla.razvan.soundmaster.Activities.MusicType;
import com.razvalla.razvan.soundmaster.Activities.SimpleObjectListActivity;
import com.razvalla.razvan.soundmaster.Model.SongInfo;

/**
 * Created by Razvan on 1/11/2015.
 */
public class ArtistListFragment  extends MusicObjectListFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        from = new String[]{MediaStore.Audio.ArtistColumns.ARTIST};
        to = new int[]{android.R.id.text1, android.R.id.text2};
        queryString = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        projection = new String[]{MediaStore.Audio.ArtistColumns.ARTIST_KEY, MediaStore.Audio.ArtistColumns.ARTIST};
        selectionArgs = new String[]{key};
    }

    @Override
    protected SimpleCursorAdapter getAdapterForMusicType(MusicType musicType) {
        SimpleCursorAdapter adapter = null;
        int layout = 0;
        layout = android.R.layout.simple_list_item_1;
        String selection = null;
        if (selection == null) {
            selectionArgs = null;
        }
        if (this.fromMusicType != MusicType.None) {
            selection = getSelection(this.fromMusicType);
        }
        adapter = new SimpleCursorAdapter(getActivity(),
                layout,
                null,
                from,
                to,
                0
        );
        return adapter;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        if (musicType != MusicType.Songs) {
            return false;
        }
        musicService = getMusicService();
        assert musicService != null;
        int menuItemIndex = item.getItemId();
        SongInfo songInfo = new SongInfo();

        Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
        String songPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
        songInfo.songPath = songPath;
        songInfo.name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME));
        songInfo.artistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
        songInfo.key = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE_KEY));
        songInfo.duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));
        songInfo.albumKey = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));

        if (menuItemIndex == 0) {
            musicService.playNow(songInfo);
        } else if (menuItemIndex == 1) {
            musicService.playNext(songInfo);
        } else if (menuItemIndex == 2) {
            musicService.addToQueue(songInfo);
        }

        return true;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        Intent intent = new Intent(getActivity().getBaseContext(), SimpleObjectListActivity.class);
        intent.putExtra("Type", MusicType.Albums);
        intent.putExtra("From", MusicType.Artists);
        Cursor cursor = (Cursor)getListAdapter().getItem(position);
        int index = cursor.getColumnIndex(MediaStore.Audio.ArtistColumns.ARTIST);
        String albumId = cursor.getString(index);
        assert albumId != null;
        intent.putExtra("Key", albumId);
        startActivity(intent);
    }
}

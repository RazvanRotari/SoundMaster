package com.razvalla.razvan.soundmaster.Fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;

import com.razvalla.razvan.soundmaster.Activities.MusicType;
import com.razvalla.razvan.soundmaster.Model.SongInfo;

/**
 * Created by Razvan on 1/11/2015.
 */
public class SongListFragment extends MusicObjectListFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        from  =  new String[]{MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.Audio.AudioColumns.ALBUM};
        to = new int[]{android.R.id.text1, android.R.id.text2};
        queryString = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        selectionArgs = new String[]{key};
    }

    @Override
    protected SimpleCursorAdapter getAdapterForMusicType(MusicType musicType) {
        SimpleCursorAdapter adapter = null;
        int layout = android.R.layout.simple_list_item_2;
        selection = getSelection(this.fromMusicType);
        if (selection == null) {
            selectionArgs = null;
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
        songInfo = getSongFromCursor(cursor);

        if (menuItemIndex == 0) {
            musicService.playNow(songInfo);
        } else if (menuItemIndex == 1) {
            musicService.playNext(songInfo);
        } else if (menuItemIndex == 2) {
            musicService.addToQueue(songInfo);
        }

        return true;
    }
}

package com.razvalla.razvan.soundmaster.Fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;

import com.razvalla.razvan.soundmaster.Activities.MusicType;
import com.razvalla.razvan.soundmaster.Model.SongInfo;
import com.razvalla.razvan.soundmaster.R;

public class VideoListFragment extends MusicObjectListFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        from  =  new String[]{MediaStore.Video.VideoColumns.DISPLAY_NAME, MediaStore.Video.VideoColumns.ARTIST};
        to = new int[]{android.R.id.text1, android.R.id.text2};
        projection = new String[]{MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.Video.VideoColumns.ARTIST,
                MediaStore.Video.VideoColumns.DURATION,
                MediaStore.Video.VideoColumns.DATA
        };
        queryString = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        String[] menuItems = getResources().getStringArray(R.array.musicListActions);
        for (int i = 0; i<menuItems.length; i++) {
            menu.add(Menu.NONE, i, i, menuItems[i]);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        musicService = getMusicService();
        assert musicService != null;
        int menuItemIndex = item.getItemId();
        SongInfo songInfo = new SongInfo();

        Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
//        projection = new String[]{MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.Video.VideoColumns.ALBUM,
//                MediaStore.Video.VideoColumns.ARTIST,
//                MediaStore.Video.VideoColumns.DURATION
//        };
        songInfo.name = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));
        songInfo.duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DURATION));
        songInfo.artistName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.ARTIST));
        songInfo.songPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA));
        songInfo.isVideo = true;

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

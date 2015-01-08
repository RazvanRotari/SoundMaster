package com.razvalla.razvan.soundmaster.Fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.razvalla.razvan.soundmaster.Activities.MusicObjectListActivity;
import com.razvalla.razvan.soundmaster.Activities.MusicType;
import com.razvalla.razvan.soundmaster.Model.SongInfo;
import com.razvalla.razvan.soundmaster.MusicService.MusicService;
import com.razvalla.razvan.soundmaster.MusicService.QueueManager;
import com.razvalla.razvan.soundmaster.R;

/**
 * Created by Razvan on 1/4/2015.
 */
public class MusicObjectListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    MusicService musicService;
    QueueManager queueManager;
    Cursor cursor;
    MusicType musicType;
    Uri queryString = null;
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static MusicObjectListFragment newInstance(MusicType musicType) {
        MusicObjectListFragment fragment = new MusicObjectListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, musicType.getValue());
        fragment.setArguments(args);
        fragment.musicType = musicType;
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadData();
        registerForContextMenu(getListView());
    }

    public void onStop() {
        super.onStop();
        if (cursor != null) {
            cursor.close();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cursor != null) {
            cursor.close();
        }
    }

    private void loadData() {

        SimpleCursorAdapter adapter = getAdapterForMusicType(musicType);
        setListAdapter(adapter);
        setListShownNoAnimation(false);
        getLoaderManager().initLoader(0, null, this);
    }

    private SimpleCursorAdapter getAdapterForMusicType(MusicType musicType) {
        SimpleCursorAdapter adapter = null;
        String[] from = new String[0];
        int[] to = new int[0];
        int layout = 0;
        switch (musicType) {
            case Artists:
                queryString = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
                from = new String[]{MediaStore.Audio.ArtistColumns.ARTIST};
                to = new int[]{android.R.id.text1};
                layout = android.R.layout.simple_list_item_1;
                break;

            case Albums:
                queryString = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
                from = new String[]{MediaStore.Audio.AlbumColumns.ALBUM, MediaStore.Audio.AlbumColumns.ARTIST};
                to = new int[]{android.R.id.text1, android.R.id.text2};
                layout = android.R.layout.simple_list_item_2;
                break;

            case Songs:
                queryString = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                from = new String[]{MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.Audio.AudioColumns.ALBUM};
                to = new int[]{android.R.id.text1, android.R.id.text2};
                layout = android.R.layout.simple_list_item_2;
                break;

            case Playlists:
                queryString = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
                from = new String[]{MediaStore.Audio.PlaylistsColumns.NAME};
                to = new int[]{android.R.id.text1};
                layout = android.R.layout.simple_list_item_1;
                break;

            case Queue:
                queryString = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                from = new String[]{MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.Audio.Media.ARTIST};
                to = new int[]{android.R.id.text1, android.R.id.text2};
                layout = android.R.layout.simple_list_item_2;
                break;
        }
        cursor = getActivity()
                .getContentResolver()
                .query(
                        queryString,
                        from,
                        null,
                        null,
                        null
                );

        adapter = new SimpleCursorAdapter(getActivity(),
                layout,
                null,
                from,
                to,
                0
        );
        return adapter;
    }
    public void onListItemClick(ListView listView, View view, int position, long id) {
    }

    //Setters & Getters
    public void setMusicService(MusicService musicService) {
        this.musicService = musicService;
        this.queueManager = musicService.getQueueManager();
    }

    MusicService getMusicService() {
        return ((MusicObjectListActivity)getActivity()).getMusicService();
    }

    QueueManager getQueueManager() {
        return getMusicService().getQueueManager();
    }

    //LoaderCallbacks
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                queryString,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        cursor.close();
        ((SimpleCursorAdapter)getListAdapter()).swapCursor(data);

        // The list should now be shown.
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }

    }
    //ContextMenu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        String[] menuItems = getResources().getStringArray(R.array.musicListActions);
        for (int i = 0; i<menuItems.length; i++) {
            menu.add(Menu.NONE, i, i, menuItems[i]);
        }
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

//        from = new String[]{MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.Audio.AudioColumns.ALBUM};
        Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
        String songPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
        songInfo.songPath = songPath;
        songInfo.name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME));
        songInfo.artistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
        songInfo.key = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE_KEY));
        songInfo.duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));
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
}

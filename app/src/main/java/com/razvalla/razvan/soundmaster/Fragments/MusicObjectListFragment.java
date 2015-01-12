package com.razvalla.razvan.soundmaster.Fragments;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ListFragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.app.LoaderManager;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.razvalla.razvan.soundmaster.Activities.MusicObjectListActivity;
import com.razvalla.razvan.soundmaster.Activities.MusicServiceProvider;
import com.razvalla.razvan.soundmaster.Activities.MusicType;
import com.razvalla.razvan.soundmaster.Model.SongInfo;
import com.razvalla.razvan.soundmaster.MusicService.MusicService;
import com.razvalla.razvan.soundmaster.MusicService.QueueManager;
import com.razvalla.razvan.soundmaster.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MusicObjectListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    int LOADER_ID = 20;
    MusicService musicService;
    QueueManager queueManager;
    MusicType musicType;
    Uri queryString = null;
    String selection = null;
    String[] selectionArgs = null;
    String[] projection = null;
    int[] to;
    String[] from;
    public String key = null;
    public MusicType fromMusicType = MusicType.None;
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static MusicObjectListFragment newInstance(MusicType musicType) {
        MusicObjectListFragment fragment = null;
        switch (musicType) {
            case Songs:
                fragment = new SongListFragment();
                break;
            case Albums:
                fragment = new AlbumListFragment();
                break;
            case Artists:
                fragment = new ArtistListFragment();
                break;
            default:
                fragment = new MusicObjectListFragment();
        }
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void loadData() {

        SimpleCursorAdapter adapter = getAdapterForMusicType(musicType);
        setListAdapter(adapter);
        setListShownNoAnimation(false);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    protected SimpleCursorAdapter getAdapterForMusicType(MusicType musicType) {
        SimpleCursorAdapter adapter = null;
        int layout = 0;
        switch (musicType) {
            case Playlists:
                queryString = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
                from = new String[]{MediaStore.Audio.PlaylistsColumns.NAME};
                to = new int[]{android.R.id.text1};
                layout = android.R.layout.simple_list_item_1;
                projection = from;
                break;

            case Queue:
                queryString = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                from = new String[]{MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.Audio.Media.ARTIST};
                to = new int[]{android.R.id.text1, android.R.id.text2};
                layout = android.R.layout.simple_list_item_2;
                break;
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
    public void onListItemClick(ListView listView, View view, int position, long id) {
    }

    protected String getSelection(MusicType musicType) {
        String field = null;
        switch (musicType) {
            case Albums:
                field = MediaStore.Audio.AudioColumns.ALBUM_KEY;
                break;
            case Artists:
                field = MediaStore.Audio.AlbumColumns.ARTIST;
        }

        String selection = null;
        if (field != null) {
            selection = String.format("%s=?", field);
        }
        return selection;
    }

    //Setters & Getters
    public void setMusicService(MusicService musicService) {
        this.musicService = musicService;
        this.queueManager = musicService.getQueueManager();
    }

    MusicService getMusicService() {
        return ((MusicServiceProvider)getActivity()).getMusicService();
    }

    QueueManager getQueueManager() {
        return getMusicService().getQueueManager();
    }

    //LoaderCallbacks
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Uri query = queryString;
        assert query != null;
        String[] fromL = null;
        try {
            if (this.projection != null) {
                List<String> fromList = new ArrayList<>();
                fromList.add("_id");
                Collections.addAll(fromList, this.projection);
                fromL = new String[fromList.size()];
                fromL = fromList.toArray(fromL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



        String sel = selection;
        String[] selArg = selectionArgs;
        if (sel == null) {
            selArg = null;
        }
        CursorLoader cursorLoader = new CursorLoader(getActivity(),
                query,
                fromL,
                sel,
                selArg,
                null);
        return cursorLoader;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((SimpleCursorAdapter) getListAdapter()).swapCursor(null);

    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        if (loader.getId() != LOADER_ID) {
            return;
        }
        ((SimpleCursorAdapter) getListAdapter()).swapCursor(data);
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
    protected SongInfo getSongFromCursor(Cursor cursor) {
        String songPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
        SongInfo songInfo = new SongInfo();
        songInfo.songPath = songPath;
        songInfo.name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME));
        songInfo.artistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
        songInfo.key = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE_KEY));
        songInfo.duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));
        songInfo.albumKey = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
        return songInfo;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        return true;
    }
}

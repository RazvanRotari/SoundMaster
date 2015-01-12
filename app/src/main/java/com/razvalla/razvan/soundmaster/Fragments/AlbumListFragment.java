package com.razvalla.razvan.soundmaster.Fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.razvalla.razvan.soundmaster.Activities.MusicType;
import com.razvalla.razvan.soundmaster.Activities.SimpleObjectListActivity;

public class AlbumListFragment extends MusicObjectListFragment{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queryString = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        from = new String[]{MediaStore.Audio.AlbumColumns.ALBUM, MediaStore.Audio.AlbumColumns.ARTIST};
        to = new int[]{android.R.id.text1, android.R.id.text2};
        projection = new String[]{MediaStore.Audio.AlbumColumns.ALBUM_KEY, MediaStore.Audio.AlbumColumns.ALBUM, MediaStore.Audio.AlbumColumns.ARTIST};
    }

    @Override
    protected SimpleCursorAdapter getAdapterForMusicType(MusicType musicType) {
        SimpleCursorAdapter adapter = null;
        int layout = android.R.layout.simple_list_item_2;
        if (this.fromMusicType != MusicType.None) {
            selection = getSelection(this.fromMusicType);
            if (selection != null) {
                selectionArgs = new String[]{key};
            } else {
                selectionArgs = null;
            }

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

       return true;
   }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        Intent intent = new Intent(getActivity().getBaseContext(), SimpleObjectListActivity.class);
        intent.putExtra("Type", MusicType.Songs);
        intent.putExtra("From", MusicType.Albums);
        Cursor cursor = (Cursor)getListAdapter().getItem(position);
        int index = cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM_KEY);
        String albumId = cursor.getString(index);
        assert albumId != null;
        intent.putExtra("Key", albumId);
        startActivity(intent);
    }
}

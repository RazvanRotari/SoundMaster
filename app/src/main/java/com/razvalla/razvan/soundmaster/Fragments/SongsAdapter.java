package com.razvalla.razvan.soundmaster.Fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.razvalla.razvan.soundmaster.Model.SongInfo;

import java.util.List;

/**
 * Created by Razvan on 1/7/2015.
 */
public class SongsAdapter extends ArrayAdapter<SongInfo> {
    public SongsAdapter(Context context, List<SongInfo> songList) {
        super(context, android.R.layout.simple_list_item_2, songList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SongInfo songInfo = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        }
        TextView songName = (TextView) convertView.findViewById(android.R.id.text1);
        TextView artistName = (TextView) convertView.findViewById(android.R.id.text2);

        songName.setText(songInfo.name);
        artistName.setText(songInfo.artistName);
        return convertView;
    }
}

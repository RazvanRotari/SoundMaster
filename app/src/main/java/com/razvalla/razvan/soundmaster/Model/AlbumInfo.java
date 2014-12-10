package com.razvalla.razvan.soundmaster.Model;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Razvan on 12/9/2014.
 */
public class AlbumInfo extends DataModel {
    List<SongInfo> songList = new ArrayList<>();
    Uri artworkPath;
    Drawable artwork;

    public Drawable getArtwork() {
        Drawable ret = null;
        if (artwork != null) {
            ret = artwork;
        }
        if (artworkPath == null) {
            //get default artwork
        } else {
            //load the artwork
        }
        return ret;
    }
    int getSongCount() {
        return songList.size();
    }

}

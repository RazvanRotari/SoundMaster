package com.razvalla.razvan.soundmaster.Model;

/**
 * Created by Razvan on 12/9/2014.
 */
public class SongInfo extends DataModel {
    public String songPath;
    public String artistName;
    public long duration;
    public String key;
    public String albumKey;
    public boolean isVideo;

    public String getSongPath() {
        return songPath;
    }
}

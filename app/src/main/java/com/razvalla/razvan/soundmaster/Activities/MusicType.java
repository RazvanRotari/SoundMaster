package com.razvalla.razvan.soundmaster.Activities;

public enum MusicType {
    Artists(0),
    Albums(1),
    Songs(2),
    Playlists(3),
    Queue(4),
    None(-1);

    private final int value;
    MusicType(int i) {
        this.value = i;
    }

    public int getValue() {
        return value;
    }
}

package com.razvalla.razvan.soundmaster.MusicService;

import android.support.annotation.NonNull;

import com.razvalla.razvan.soundmaster.Model.SongInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Razvan on 1/4/2015.
 */
public class QueueManager implements List<SongInfo> {
    List<SongInfo> queue = new ArrayList<>();
    int currentIndex = 0;
    boolean repeat = false;

    public SongInfo getCurrentSong() {
        return queue.get(currentIndex);
    }

    public int getCurrentSongIndex() {
        return currentIndex;
    }

    public SongInfo nextSong() {
        if (queue.size() - 1 == currentIndex && repeat) {
            currentIndex = 0;
        } else if (queue.size() - 1 == currentIndex && !repeat) {
            return null;
        } else {
            currentIndex++;
        }
        return queue.get(currentIndex);
    }

    public SongInfo previousSong() {
        if (currentIndex == 0) {
            return null;
        }
        currentIndex--;
        return queue.get(currentIndex);
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public void playNow(SongInfo songInfo) {
        queue.add(currentIndex, songInfo);
    }
    public void playNext(SongInfo songInfo) {
        queue.add(currentIndex + 1, songInfo);
    }

    public void addToQueue(SongInfo songInfo) {
        queue.add(songInfo);
    }

    //List
    @Override
    public void add(int location, SongInfo object) {
        queue.add(location, object);
    }

    @Override
    public boolean add(SongInfo object) {
        return queue.add(object);
    }

    @Override
    public boolean addAll(int location, Collection<? extends SongInfo> collection) {
        return queue.addAll(location, collection);
    }

    @Override
    public boolean addAll(Collection<? extends SongInfo> collection) {
        return queue.addAll(collection);
    }

    @Override
    public void clear() {
        queue.clear();
    }

    @Override
    public boolean contains(Object object) {
        return contains(object);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return queue.containsAll(collection);
    }

    @Override
    public SongInfo get(int location) {
        return queue.get(location);
    }

    @Override
    public int indexOf(Object object) {
        return queue.indexOf(object);
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @NonNull
    @Override
    public Iterator<SongInfo> iterator() {
        return queue.iterator();
    }

    @Override
    public int lastIndexOf(Object object) {
        return queue.lastIndexOf(object);
    }

    @NonNull
    @Override
    public ListIterator<SongInfo> listIterator() {
        return queue.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<SongInfo> listIterator(int location) {
        return queue.listIterator(location);
    }

    @Override
    public SongInfo remove(int location) {
        return queue.remove(location);
    }

    @Override
    public boolean remove(Object object) {
        return queue.remove(object);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return queue.removeAll(collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return queue.retainAll(collection);
    }

    @Override
    public SongInfo set(int location, SongInfo object) {
        return queue.set(location, object);
    }

    @Override
    public int size() {
        return queue.size();
    }

    @NonNull
    @Override
    public List<SongInfo> subList(int start, int end) {
        return queue.subList(start, end);
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return queue.toArray();
    }

    @NonNull
    @Override
    public <T> T[] toArray(T[] array) {
        return queue.toArray(array);
    }
}

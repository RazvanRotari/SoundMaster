package com.razvalla.razvan.soundmaster.Activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;

import com.razvalla.razvan.soundmaster.Fragments.MusicObjectListFragment;
import com.razvalla.razvan.soundmaster.MusicService.MusicService;
import com.razvalla.razvan.soundmaster.R;

/**
 * Created by Razvan on 1/11/2015.
 */
public class SimpleObjectListActivity extends FragmentActivity implements MusicServiceProvider {
    MusicObjectListFragment musicObjectListFragment;

    MusicService mMusicService;
    MusicObjectListFragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_music_list);
        Intent intent = getIntent();
        MusicType musicType = (MusicType)intent.getSerializableExtra("Type");
        MusicType fromMusicType = (MusicType)intent.getSerializableExtra("From");

        fragment = MusicObjectListFragment.newInstance(musicType);
        fragment.key = intent.getStringExtra("Key");
        fragment.fromMusicType = fromMusicType;
        musicObjectListFragment = fragment;

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }


        class MusicConnection implements ServiceConnection {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mMusicService = ((MusicService.MusicBinder) service).getService();
                fragment.setMusicService(mMusicService);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mMusicService = null;
            }
        }
        boolean ret = bindService(new Intent(getBaseContext(), MusicService.class), new MusicConnection(), BIND_AUTO_CREATE);
        assert ret;
    }

    @Override
    public MusicService getMusicService() {
        return mMusicService;
    }
}

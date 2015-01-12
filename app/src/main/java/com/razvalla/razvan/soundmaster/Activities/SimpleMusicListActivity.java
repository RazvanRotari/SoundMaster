package com.razvalla.razvan.soundmaster.Activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import com.razvalla.razvan.soundmaster.Fragments.MusicObjectListFragment;
import com.razvalla.razvan.soundmaster.Fragments.SongListFragment;
import com.razvalla.razvan.soundmaster.MusicService.MusicService;
import com.razvalla.razvan.soundmaster.R;


public class SimpleMusicListActivity extends ActionBarActivity implements MusicServiceProvider {

    MusicService mMusicService;
    ServiceConnection mMusicConnection;

    @Override
    public void onStart() {
        super.onStart();
        if (mMusicService == null) {
            class MusicConnection implements ServiceConnection {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    mMusicService = ((MusicService.MusicBinder)service).getService();
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    mMusicService = null;
                }
            }
            mMusicConnection = new MusicConnection();
            boolean ret = bindService(new Intent(getBaseContext(), MusicService.class), mMusicConnection, BIND_AUTO_CREATE);
            assert ret;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_music_list);
        Intent intent = getIntent();
        int type = intent.getIntExtra("Type", 0);
        MusicType musicType = MusicType.values()[type];
        MusicObjectListFragment fragment = MusicObjectListFragment.newInstance(musicType);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_simple_music_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public MusicService getMusicService() {
        return mMusicService;
    }
}

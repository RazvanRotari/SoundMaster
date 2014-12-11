package com.razvalla.razvan.soundmaster;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.razvalla.razvan.soundmaster.Fragments.CurrentPlaylistFragment;
import com.razvalla.razvan.soundmaster.MusicService.MusicService;


public class MusicPlayer extends ActionBarActivity {
    MusicService mMusicService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        if (savedInstanceState == null) {
        }
        class MusicConnection implements ServiceConnection {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mMusicService = ((MusicService.MusicBinder)service).getService();
                CurrentPlaylistFragment fragment = (CurrentPlaylistFragment)getFragmentManager().findFragmentById(R.id.playerFragment);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_music_player, menu);
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
}

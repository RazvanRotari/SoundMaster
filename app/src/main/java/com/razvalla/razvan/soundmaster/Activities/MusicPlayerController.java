package com.razvalla.razvan.soundmaster.Activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.Image;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.razvalla.razvan.soundmaster.Model.SongInfo;
import com.razvalla.razvan.soundmaster.MusicService.MusicService;
import com.razvalla.razvan.soundmaster.R;

public class MusicPlayerController extends ActionBarActivity {

    ImageButton playButton;
    ImageButton nextButton;
    ImageButton previousButton;
    TextView songTextView;
    TextView artistTextView;
    SeekBar seekBar;
    TextView currentTime;
    TextView songDuration;
    ImageView artworkImageView;

    MusicService musicService;
    ServiceConnection musicConnection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player_controller);
        if (savedInstanceState == null) {
            playButton = (ImageButton)findViewById(R.id.PlayButton);
            previousButton = (ImageButton)findViewById(R.id.previousButton);
            nextButton = (ImageButton)findViewById(R.id.NextButton);
            songTextView = (TextView)findViewById(R.id.song_label);
            artistTextView = (TextView)findViewById(R.id.artist_label);
            currentTime = (TextView)findViewById(R.id.currentTime);
            songDuration = (TextView)findViewById(R.id.totalDuration);
            seekBar = (SeekBar)findViewById(R.id.progressBar);
            artworkImageView = (ImageView)findViewById(R.id.artworkImageView);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (musicService == null) {
            class MusicConnection implements ServiceConnection {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    musicService = ((MusicService.MusicBinder)service).getService();
                    if (musicService.isPlaying()) {
                        playButton.setBackgroundResource(android.R.drawable.ic_media_pause);
                    } else {
                        playButton.setBackgroundResource(android.R.drawable.ic_media_play);
                    }
                    SongInfo songInfo = musicService.getQueueManager().getCurrentSong();
                    setLabels(songInfo);
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    musicService = null;
                }
            }
            musicConnection = new MusicConnection();
            boolean ret = bindService(new Intent(getBaseContext(), MusicService.class), musicConnection, BIND_AUTO_CREATE);
            assert ret;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_music_player_controller, menu);
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

    //Controls
    public void previousButtonPressed(View view) {
        musicService.playPrevious();
    }

    public void nextButtonPressed(View view) {
        musicService.playNext();
    }

    public void playButtonPressed(View view) {
        if (musicService.isPlaying()) {
            musicService.pause();
            playButton.setBackgroundResource(android.R.drawable.ic_media_play);
        } else {
            musicService.play();
            playButton.setBackgroundResource(android.R.drawable.ic_media_pause);
        }
    }

    void setLabels(SongInfo songInfo) {
        artistTextView.setText(songInfo.artistName);
        songTextView.setText(songInfo.name);
        songDuration.setText(songInfo.duration);
        //artworkImageView.set
    }
}

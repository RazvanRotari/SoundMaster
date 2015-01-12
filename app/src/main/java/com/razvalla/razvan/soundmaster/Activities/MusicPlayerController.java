package com.razvalla.razvan.soundmaster.Activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.razvalla.razvan.soundmaster.Model.SongInfo;
import com.razvalla.razvan.soundmaster.MusicService.MusicService;
import com.razvalla.razvan.soundmaster.R;

public class MusicPlayerController extends ActionBarActivity implements MusicService.OnNextSong, SurfaceHolder.Callback {

    ImageButton playButton;
    ImageButton nextButton;
    ImageButton previousButton;
    TextView songTextView;
    TextView artistTextView;
    SeekBar seekBar;
    TextView currentTimeTextView;
    TextView songDuration;
    ImageView artworkImageView;
    SurfaceView surfaceView;

    MusicService musicService;
    ServiceConnection musicConnection;
    long currentTime;
    Handler progressTimer;
    SurfaceHolder holder;
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
            currentTimeTextView = (TextView)findViewById(R.id.currentTime);
            songDuration = (TextView)findViewById(R.id.totalDuration);
            artworkImageView = (ImageView)findViewById(R.id.artworkImageView);
            surfaceView = (SurfaceView)findViewById(R.id.surfaceView);
            holder = surfaceView.getHolder();
            seekBar = (SeekBar)findViewById(R.id.progressBar);
            progressTimer = new Handler();
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (!fromUser) return;
                    currentTime = progress * 1000;
                    updateTime();
                    if (musicService == null) {
                        return;
                    }
                    musicService.seek(currentTime);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
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
                    if (playButton == null) {
                        return;
                    }
                    if (musicService.isPlaying()) {
                        playButton.setBackgroundResource(android.R.drawable.ic_media_pause);
                    } else {
                        playButton.setBackgroundResource(android.R.drawable.ic_media_play);
                    }
                    musicService.onNextSong =  MusicPlayerController.this;
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
        holder.addCallback(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        musicService.setSurfaceHolder(null);
        holder.removeCallback(this);
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
        if (musicService.playPrevious()) {
            setLabels(musicService.getQueueManager().getCurrentSong());
        }
        startTime();
    }

    public void nextButtonPressed(View view) {
        if (musicService.playNext()) {
            setLabels(musicService.getQueueManager().getCurrentSong());
        }
        startTime();
    }

    public void playButtonPressed(View view) {
        if (musicService.isPlaying()) {
            musicService.pause();
            playButton.setBackgroundResource(android.R.drawable.ic_media_play);
        } else {
            musicService.play();
            playButton.setBackgroundResource(android.R.drawable.ic_media_pause);
            startTime();
        }
    }

    void setLabels(SongInfo songInfo) {
        if (songInfo.isVideo) {
            surfaceView.setVisibility(View.VISIBLE);
            artworkImageView.setVisibility(View.INVISIBLE);
        } else {
            surfaceView.setVisibility(View.INVISIBLE);
            artworkImageView.setVisibility(View.VISIBLE);
            Uri artwork = getArtworkForSong(songInfo);
            if (artwork != null) {
                artworkImageView.setImageURI(artwork);
            } else {
                artworkImageView.setImageResource(R.drawable.default_artwork);
            }
        }

        if (songInfo.artistName != null) {
            artistTextView.setText(songInfo.artistName);
        } else {
            artistTextView.setText("");
        }
        songTextView.setText(songInfo.name);
        long minutes = songInfo.duration / 1000 / 60;
        long seconds = (songInfo.duration / 1000) % 60;
        String duration = String.format("%d:%02d", minutes, seconds);
        songDuration.setText(duration);
        int max = (int) (songInfo.duration / 1000);
        seekBar.setMax(max);
        seekBar.setProgress(0);
        currentTimeTextView.setText("0:00");
        currentTime = 0;
    }

    Uri getArtworkForSong(SongInfo songInfo) {
        String[] projection = new String[]{"_id", MediaStore.Audio.AlbumColumns.ALBUM_ART};
        String selection = String.format("%s = ?", MediaStore.Audio.AlbumColumns.ALBUM_KEY);
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                new String[]{songInfo.albumKey},
                null);
        if (cursor == null) {
            return null;
        }
        if(!cursor.moveToFirst()) {
            return null;
        }
        int index = cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM_ART);
        String path = cursor.getString(index);
        Uri art = Uri.parse(path);
        return art;
    }


    @Override
    public void onNextSong(SongInfo songInfo) {
        setLabels(songInfo);
        startTime();
    }

    void startTime() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (musicService.isPlaying()) {
                    currentTime+= 1000;
                    updateTime();
                    progressTimer.postDelayed(this, 1000);
                }
            }
        };
        progressTimer.postDelayed(runnable, 100);
    }

    void updateTime() {
        seekBar.setProgress((int) (currentTime / 1000));
        long minutes = currentTime / 1000 / 60;
        long seconds = (currentTime / 1000) % 60;
        String duration = String.format("%d:%02d", minutes, seconds);
        currentTimeTextView.setText(duration);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (musicService != null) musicService.setSurfaceHolder(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (musicService != null) musicService.setSurfaceHolder(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (musicService != null) musicService.setSurfaceHolder(null);

    }
}

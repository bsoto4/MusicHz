package os.musichz;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by usuario on 16/07/2016.
 */
public class PlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btnBack, btnRewind, btnPlayPause, btnForward, btnNext, btnPlaylist;
    private SeekBar sbMusic;
    private TextView tvMusic, tvTime;
    private static MediaPlayer mediaPlayer;
    private ArrayList<File> musicFiles;
    private Thread sbRunMusic;
    private Intent intent;
    private Bundle bundle;
    private int position;
    private Uri uri;
    private String aux = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_activity);
        btnBack = (ImageButton) findViewById(R.id.btn_back);
        btnRewind = (ImageButton) findViewById(R.id.btn_rewind);
        btnPlayPause = (ImageButton) findViewById(R.id.btn_play_pause);
        btnForward = (ImageButton) findViewById(R.id.btn_forward);
        btnNext = (ImageButton) findViewById(R.id.btn_next);
        btnPlaylist = (ImageButton) findViewById(R.id.btn_playlist);
        sbMusic = (SeekBar) findViewById(R.id.sb_music);
        tvMusic = (TextView) findViewById(R.id.tv_music);
        tvTime = (TextView) findViewById(R.id.tv_time);

        btnBack.setOnClickListener(this);
        btnRewind.setOnClickListener(this);
        btnPlayPause.setOnClickListener(this);
        btnForward.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPlaylist.setOnClickListener(this);

        sbRunMusic = new Thread() {
            @Override
            public void run() {
                int duration = mediaPlayer.getDuration();
                sbMusic.setMax(duration);
                int actualPosition = 0;
                int actionPosition = 0;

                boolean status = false;

                while (actualPosition < duration) {
                    try {
                        sleep(500);
                        actualPosition = mediaPlayer.getCurrentPosition();
                        sbMusic.setProgress(actualPosition);
                        tvTime.setText(getTime(actionPosition));

                    } catch (Exception e) {
                        tvTime.setText("--:--");
                    }
                }
            }
        };

        if (mediaPlayer != null) {
            sbRunMusic.stop();
        }

        try {
            intent = getIntent();
            bundle = intent.getExtras();
            musicFiles = (ArrayList) bundle.getParcelableArrayList("MUSICFILES");
            position = bundle.getInt("POSITION", 0);
            uri = Uri.parse(musicFiles.get(position).toString());
            tvMusic.setText(musicFiles.get(position).getName().toString());
            mediaPlayer = MediaPlayer.create(getApplication(), uri);
            sbRunMusic.start();
            mediaPlayer.start();
            changeVolumen();
        } catch (Exception e) {

        }

        sbMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
    }

    private void changeVolumen() {

    }

    private String getTime(int milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
        return ((hours < 10) ? "0" + hours : hours) + ":" +
                ((minutes < 10) ? "0" + minutes : minutes) + ":" +
                ((seconds < 10) ? "0" + seconds : seconds);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_back:
                previusMusic();
                break;
            case R.id.btn_rewind:
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-5000);
                break;
            case R.id.btn_play_pause:
                playPause();
                break;
            case R.id.btn_forward:
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+5000);
                break;
            case R.id.btn_next:
                netxMusic();
                break;
            case R.id.btn_playlist:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
        }
    }

    private void previusMusic() {
        mediaPlayer.stop();
        if (position - 1 < 0) {
            position = musicFiles.size() - 1;
        } else {
            position = position - 1;
        }
        tvMusic.setText(musicFiles.get(position).getName().toString());
        uri = Uri.parse(musicFiles.get(position).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
        tvTime.setText(getTime(mediaPlayer.getDuration()));
        sbMusic.setMax(mediaPlayer.getDuration());
    }

    private void netxMusic() {
        mediaPlayer.stop();
        position = (position + 1) % musicFiles.size();
        tvMusic.setText(musicFiles.get(position).getName().toString());
        uri = Uri.parse(musicFiles.get(position).toString());
        mediaPlayer = MediaPlayer.create(getApplication(), uri);
        mediaPlayer.start();
        tvTime.setText(getTime(mediaPlayer.getDuration()));
        sbMusic.setMax(mediaPlayer.getDuration());

    }

    private void playPause() {
        if (mediaPlayer.isPlaying()) {
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            mediaPlayer.pause();
        } else {
            btnPlayPause.setImageResource(R.drawable.ic_play_white);
            mediaPlayer.start();
        }
    }
}

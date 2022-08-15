package com.tobi.simplemusicapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.material.slider.Slider;
import com.tobi.simplemusicapp.databinding.ActivityMainBinding;
import com.tobi.simplemusicapp.databinding.SongcoverFragmentBinding;

import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private View songFragmentView;
    private View songListFragmentView;
    private View playlistListsFragmentView;

    private ActivityMainBinding binding;

    private boolean isPlaying = false;
    private Handler mHandler;



    private SimpleMediaPlayer simpleMediaPlayer;

    int currentSongIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //TODO:: temp
        simpleMediaPlayer = new SimpleMediaPlayer(this);
        ArrayList<Song> songs = simpleMediaPlayer.getAllSongs();

        binding.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(simpleMediaPlayer.getRunningSong() == null){
                    PlaySong(songs.get(0));
                    binding.playButton.setImageResource(android.R.drawable.ic_media_pause);
                    return;
                }

                if(simpleMediaPlayer.getMediaPlayer().isPlaying())
                    simpleMediaPlayer.getMediaPlayer().pause();
                else{
                    simpleMediaPlayer.getMediaPlayer().start();
                }

                isPlaying = !isPlaying;

                binding.playButton.setImageResource(isPlaying
                        ? android.R.drawable.ic_media_pause
                        : android.R.drawable.ic_media_play);
            }
        });

        binding.nextSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(currentSongIndex + 1 > songs.size())
                    return;

                PlayNextSong(songs.get(++currentSongIndex), false);
            }
        });

        binding.prevSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(currentSongIndex - 1 < 0)
                    return;

                PlayNextSong(songs.get(--currentSongIndex), true);
            }
        });

        binding.timelineSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {

            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                binding.currentPosText.setText(formatDuration((int)slider.getValue()));
                simpleMediaPlayer.getMediaPlayer().seekTo((int)slider.getValue());
            }
        });

        mHandler = new Handler();
        MainActivity.this.runOnUiThread(new Runnable(){
            @Override
            public void run() {
                if(simpleMediaPlayer.getMediaPlayer().isPlaying()){
                    int currentPos = simpleMediaPlayer.getMediaPlayer().getCurrentPosition();
                    binding.timelineSlider.setValue(currentPos);
                    binding.currentPosText.setText(formatDuration(currentPos));
                }
                mHandler.postDelayed(this, 1000);
            }
        });

        LinearLayout mainLayout = binding.fragmentHolder;
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(songFragmentView == null)
            songFragmentView = inflater.inflate(R.layout.songcover_fragment, mainLayout, false);

        ShowSongInfo(songs.get(0));

        ShowFragment(songFragmentView, mainLayout);
        binding.showPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(playlistListsFragmentView == null)
                    playlistListsFragmentView = inflater.inflate(R.layout.fragment_playlist_lists, mainLayout, false);

                ShowFragment(playlistListsFragmentView, mainLayout);
            }
        });

        binding.showSongsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(songListFragmentView == null)
                    songListFragmentView = inflater.inflate(R.layout.playlist_fragment, mainLayout, false);

                ShowFragment(songListFragmentView, mainLayout);
            }
        });

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShowFragment(songFragmentView, mainLayout);
            }
        });

        simpleMediaPlayer.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
                binding.timelineSlider.setValue(0);
                binding.playButton.setImageResource(android.R.drawable.ic_media_play);
                binding.currentPosText.setText("00:00");
                isPlaying = false;
            }
        });
    }

    private String formatDuration(int duration) {
        long minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS);
        long seconds = TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS)
                - minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES);

        return String.format("%02d:%02d", minutes, seconds);
    }

    public void PlaySong(Song song){

        //Resetting the Timeline
        binding.timelineSlider.setValue(0);
        binding.currentPosText.setText("00:00");

        isPlaying = true;
        binding.playButton.setImageResource(android.R.drawable.ic_media_pause);

        ShowSongInfo(song);
        binding.timelineSlider.setValueTo(song.getDuration());
        binding.durationText.setText(formatDuration(song.getDuration()));
        simpleMediaPlayer.play(song);
    }

    public void ShowSongInfo(Song song){
        SongcoverFragmentBinding songCoverBinding = SongcoverFragmentBinding.bind(songFragmentView);
        songCoverBinding.songTitle.setText(song.getTitle());
        songCoverBinding.songArtist.setText(song.getArtist());
    }

    public void PlayNextSong(Song song, boolean next){

        if(binding.fragmentHolder.getChildAt(0) != songFragmentView)
            return;

        int translationX = next ? -520 : 520;

        binding.fragmentHolder.setVisibility(View.INVISIBLE);
        //Not the best way
        binding.fragmentHolder.setTranslationX(translationX);
        binding.fragmentHolder.setVisibility(View.VISIBLE);

        binding.fragmentHolder.animate().setDuration(350).translationX(0);

        simpleMediaPlayer.getMediaPlayer().reset();

        PlaySong(song);
    }

    public void ShowFragment(View view, LinearLayout layout){

        //Changing the visibility of the toggle button
        if(view != songFragmentView){
            binding.showSongsButton.setVisibility(View.INVISIBLE);
            binding.backButton.setVisibility(View.VISIBLE);
        }
        else{
            binding.showSongsButton.setVisibility(View.VISIBLE);
            binding.backButton.setVisibility(View.INVISIBLE);
        }

        layout.setVisibility(View.INVISIBLE);
        //Not the best way
        layout.setTranslationY(520);
        layout.setVisibility(View.VISIBLE);

        //Removing the old view
        layout.removeView(layout.getChildAt((0)));
        layout.addView(view, 0);

        //Animating the popup
        layout.animate().setDuration(250).translationY(0);
    }
}
package com.tobi.simplemusicapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;

import com.google.android.material.slider.Slider;
import com.tobi.simplemusicapp.databinding.ActivityMainBinding;

import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private View songFragmentView;
    private View songListFragmentView;
    private View playlistListsFragmentView;

    private ActivityMainBinding binding;

    SimpleMediaPlayer simpleMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //TODO:: temp
        simpleMediaPlayer = new SimpleMediaPlayer(this);
        ArrayList<Song> songs = simpleMediaPlayer.getAllSongs();

        binding.timelineSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {

            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                binding.durationText.setText(Float.toString(slider.getValue()));
            }
        });

        LinearLayout mainLayout = binding.fragmentHolder;
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(songFragmentView == null)
            songFragmentView = inflater.inflate(R.layout.songcover_fragment, mainLayout, false);

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
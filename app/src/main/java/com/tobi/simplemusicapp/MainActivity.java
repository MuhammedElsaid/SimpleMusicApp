package com.tobi.simplemusicapp;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.IBinder;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.slider.Slider;
import com.tobi.simplemusicapp.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private View songFragmentView;
    private View songListFragmentView;
    private View playlistListsFragmentView;

    private ActivityMainBinding binding;

    SimpleMediaPlayer simpleMediaPlayer;

    private void main(){

        FeedReaderDbHelper FD = new FeedReaderDbHelper(getApplicationContext(),"Ay7aga",null,1);
        SQLiteDatabase DB= FD.getReadableDatabase();
        FD.insertDataPlaylist("1st");
        String[] Projection ={FeedReaderContract.FeedEntry.COLUMN_NAME_nID, FeedReaderContract.FeedEntry.COLUMN_NAME_n};
        Cursor cursor = DB.query(
                FeedReaderContract.FeedEntry.TABLE_NAMEn,
                Projection,
                null,
                null,
                null,
                null,
                null
        );

        List itemIds = new ArrayList<Integer>();
        while(cursor.moveToNext()) {
            long itemId = cursor.getInt(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_nID));
            itemIds.add(itemId);
        }

        cursor.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        simpleMediaPlayer = new SimpleMediaPlayer(this);
        //ArrayList<Song> songs = simpleMediaPlayer.getAllSongs();

        //simpleMediaPlayer.play(songs.get(3));

        main();
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
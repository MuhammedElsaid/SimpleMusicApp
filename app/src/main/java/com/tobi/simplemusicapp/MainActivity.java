package com.tobi.simplemusicapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.transition.Scene;
import android.transition.Visibility;
import android.util.DisplayMetrics;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.slider.Slider;
import com.tobi.simplemusicapp.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowMetrics;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private View songFragmentView;
    private View songListFragmentView;
    private View playlistListsFragmentView;

    FloatingActionButton showSongFragmentButton;
    FloatingActionButton backButton;

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Slider timelineSlider = findViewById(R.id.timelineSlider);
        TextView currentDurationText = findViewById(R.id.durationText);

        timelineSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {

            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                currentDurationText.setText(Float.toString(slider.getValue()));
            }
        });

        showSongFragmentButton = findViewById(R.id.showSongsButton);
        backButton = findViewById(R.id.backButton);

        FloatingActionButton showPlaylistButton = findViewById(R.id.showPlaylistButton);

        AbsoluteLayout mainLayout = findViewById(R.id.fragmentHolder);
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(songFragmentView == null)
            songFragmentView = inflater.inflate(R.layout.fragment_second, mainLayout, false);

        ShowFragment(songFragmentView, mainLayout);

        showPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(playlistListsFragmentView == null)
                    playlistListsFragmentView = inflater.inflate(R.layout.fragment_playlist_lists, mainLayout, false);

                ShowFragment(playlistListsFragmentView, mainLayout);

            }
        });

        showSongFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(songListFragmentView == null)
                    songListFragmentView = inflater.inflate(R.layout.fragment_first, mainLayout, false);

                ShowFragment(songListFragmentView, mainLayout);

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShowFragment(songFragmentView, mainLayout);
            }
        });
    }

    public void ShowFragment(View view, AbsoluteLayout layout){

        if(view != songFragmentView){
            showSongFragmentButton.setVisibility(View.INVISIBLE);
            backButton.setVisibility(View.VISIBLE);
        }
        else{
            showSongFragmentButton.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.INVISIBLE);
        }

        layout.setVisibility(View.INVISIBLE);
        layout.setTranslationY(520);
        layout.setVisibility(View.VISIBLE);

        layout.removeView(layout.getChildAt((0)));
        layout.addView(view, 0);

        layout.animate().setDuration(300).translationY(50);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
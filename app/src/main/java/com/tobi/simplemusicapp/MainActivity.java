package com.tobi.simplemusicapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.slider.Slider;
import com.tobi.simplemusicapp.databinding.ActivityMainBinding;
import com.tobi.simplemusicapp.databinding.FragmentPlaylistListsBinding;
import com.tobi.simplemusicapp.databinding.PlaylistFragmentBinding;
import com.tobi.simplemusicapp.databinding.PlaylistListviewBinding;
import com.tobi.simplemusicapp.databinding.SongcoverFragmentBinding;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {

    private View songFragmentView;
    private View songListFragmentView;
    private View playlistListsFragmentView;

    private ActivityMainBinding binding;

    private boolean isPlaying = false;
    private Handler mHandler;

    private SimpleMediaPlayer simpleMediaPlayer;
    private MusicPlayerDBHelper musicPlayerDBHelper;

    private Playlist currentPlaylist;
    private Playlist allSongsPlaylist;

    private ArrayList<Playlist> playlists;
    private ArrayAdapter<String> playlistsAdapter;

    int currentSongIndex = 0;

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.addToPlaylist:{

                AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

                ListView listview = PlaylistFragmentBinding.bind(songListFragmentView).songsList;

                Song song = (Song)listview.getItemAtPosition(acmi.position);

                ArrayList<String> playlistNames = new ArrayList<>();

                playlists.forEach(playlist -> {
                    playlistNames.add(playlist.getTitle());
                });


                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Pick a playlist");
                builder.setItems(playlistNames.toArray(new String[playlistNames.size()]), (dialog, which) -> {

                    Playlist playlist = playlists.get(which);
                    playlist.add(song);
                    musicPlayerDBHelper.insertDataSong(String.valueOf(song.getId()), String.valueOf(playlist.getID()));
                });
                builder.show();

                break;
            }

        }

        return super.onContextItemSelected(item);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.songs_list_menu, menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        musicPlayerDBHelper = new MusicPlayerDBHelper(getApplicationContext(), "MusicPlayer", null, 1);

        //TODO:: temp
        simpleMediaPlayer = new SimpleMediaPlayer(this);
        allSongsPlaylist = simpleMediaPlayer.getAllSongs();
        currentPlaylist = allSongsPlaylist;

        binding.playButton.setOnClickListener(view -> {

            if(simpleMediaPlayer.getRunningSong() == null){
                PlaySong(currentPlaylist.get(0));
                binding.playButton.setImageResource(android.R.drawable.ic_media_pause);
                return;
            }

            if(simpleMediaPlayer.getMediaPlayer().isPlaying())
                simpleMediaPlayer.getMediaPlayer().pause();
            else
                simpleMediaPlayer.getMediaPlayer().start();

            isPlaying = !isPlaying;

            binding.playButton.setImageResource(isPlaying
                    ? android.R.drawable.ic_media_pause
                    : android.R.drawable.ic_media_play);
        });

        binding.nextSongButton.setOnClickListener(view -> {

            if(currentSongIndex + 1 > currentPlaylist.size() - 1)
                return;

            PlayNextSong(currentPlaylist.get(++currentSongIndex), false);
        });

        binding.prevSongButton.setOnClickListener(view -> {

            if(currentSongIndex - 1 < 0)
                return;

            PlayNextSong(currentPlaylist.get(--currentSongIndex), true);
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

        songFragmentView = inflater.inflate(R.layout.songcover_fragment, mainLayout, false);
        playlistListsFragmentView = inflater.inflate(R.layout.fragment_playlist_lists, mainLayout, false);
        songListFragmentView = inflater.inflate(R.layout.playlist_fragment, mainLayout, false);

        //Setting our playlistview
        SetPlaylistView(allSongsPlaylist);

        FragmentPlaylistListsBinding playlistListviewBinding = FragmentPlaylistListsBinding.bind(playlistListsFragmentView);

        SetPlaylists();
        playlistListviewBinding.playlistsList.setAdapter(playlistsAdapter);

        playlistListviewBinding.playlistsList.setOnItemClickListener((adapterView, view12, i, l) -> {

            currentPlaylist =  i == 0 ? allSongsPlaylist : playlists.get(i - 1);
            SetPlaylistView(currentPlaylist);

            ShowFragment(songListFragmentView, mainLayout);
        });

        playlistListviewBinding.addPlaylistButton.setOnClickListener(view1 -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Insert playlist name");

            final EditText editTextName1 = new EditText(MainActivity.this);
            editTextName1.setHint("Playlist name");

            builder.setView(editTextName1);
            LinearLayout layoutName = new LinearLayout(MainActivity.this);
            layoutName.setOrientation(LinearLayout.VERTICAL);
            layoutName.addView(editTextName1);
            builder.setView(layoutName);

            builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                dialog.dismiss();

                String playlistName = editTextName1.getText().toString();
                musicPlayerDBHelper.insertDataPlaylist(playlistName);

                playlists.add(new Playlist( playlistName, musicPlayerDBHelper.getLastId()));
                playlistsAdapter.add(playlistName);
            });

            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());
            builder.show();
        });

        if(currentPlaylist.size() > 0)
            ShowSongInfo(currentPlaylist.get(0));

        ShowFragment(songFragmentView, mainLayout);
        binding.showPlaylistButton.setOnClickListener(view -> {

            ShowFragment(playlistListsFragmentView, mainLayout);
        });

        binding.showSongsButton.setOnClickListener(view -> {

            ShowFragment(songListFragmentView, mainLayout);
        });

        binding.backButton.setOnClickListener(view -> ShowFragment(songFragmentView, mainLayout));

        simpleMediaPlayer.getMediaPlayer().setOnCompletionListener(mediaPlayer -> {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            binding.timelineSlider.setValue(0);
            binding.playButton.setImageResource(android.R.drawable.ic_media_play);
            binding.currentPosText.setText("00:00");
            isPlaying = false;
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

        simpleMediaPlayer.getMediaPlayer().reset();
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

    public void SetPlaylistView(Playlist playlist){

        PlaylistFragmentBinding playlistFragmentBinding = PlaylistFragmentBinding.bind(songListFragmentView);
        PlaylistAdapter adapter = new PlaylistAdapter(this, playlist);
        playlistFragmentBinding.songsList.setAdapter(adapter);

        registerForContextMenu(playlistFragmentBinding.songsList);

        playlistFragmentBinding.playlistName.setText(playlist.getTitle());
        playlistFragmentBinding.numberOfSongsTxt.setText(String.valueOf(playlist.size()));
        playlistFragmentBinding.songsList.setOnItemClickListener((adapterView, view, i, l) -> {

            currentSongIndex = i;
            PlaySong(playlist.get(i));
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

    public void SetPlaylists(){

        playlists = musicPlayerDBHelper.getPlaylists(simpleMediaPlayer);
        playlistsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        playlistsAdapter.add("All songs");

        playlists.forEach(playlist -> playlistsAdapter.add(playlist.getTitle()));
    }
}
package com.tobi.simplemusicapp;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Size;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class SimpleMediaPlayer {

    private MediaPlayer mediaPlayer;
    private Activity activity;

    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 41;

    public SimpleMediaPlayer(Activity activity) {
        this.mediaPlayer = new MediaPlayer();
        this.activity = activity;

        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
    }

    int currentSongIndex;

    public void playPlaylist(Playlist playlist){

        currentSongIndex = 0;
        play(playlist.get(currentSongIndex));

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                currentSongIndex++;
                play(playlist.get(currentSongIndex));

                if(playlist.size() == currentSongIndex)
                    currentSongIndex = 0;
            }
        });
    }

    public void play(Song song){

        try
        {
            mediaPlayer.setDataSource(activity.getApplicationContext(), Uri.parse(song.getPath()));
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException exception){
            //TODO:: handle exception
        }
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = activity.getApplicationContext().checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    ArrayList<Song> getAllSongs(){

        if(!checkPermissionForReadExtertalStorage()){
            try {
                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_STORAGE_PERMISSION_REQUEST_CODE);
            } catch (Exception e) {
                Toast.makeText(activity.getApplicationContext(),
                        "Permission to read external storage has been denied", Toast.LENGTH_SHORT).show();
            }
        }

        ArrayList<Song> foundSongs = new ArrayList<>();

        ContentResolver contentResolve = activity.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Images.ImageColumns._ID
        };
        Cursor cursor = contentResolve.query(uri, projection, null, null, null);

        if(cursor != null && cursor.moveToFirst()){

            do {
                String title = cursor.getString(0);
                String artistName = cursor.getString(1);
                String path = cursor.getString(2);
                int imageId = cursor.getInt(3);

                foundSongs.add(new Song(path, title, artistName, imageId));
            }
            while(cursor.moveToNext());
        }

        return foundSongs;
    }

}

package com.tobi.simplemusicapp;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class SimpleMediaPlayer {

    private MediaPlayer mediaPlayer;
    private Activity activity;

    private Song runningSong;

    public Song getRunningSong() {
        return runningSong;
    }

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
            mediaPlayer.setDataSource(activity.getApplicationContext(), song.getPath());
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    runningSong = song;
                    mediaPlayer.start();
                }
            });

        } catch (IOException exception){
            exception.printStackTrace();
            //TODO:: handle exception
        }
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public boolean checkPermissionForReadExternalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = activity.getApplicationContext().checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    private void showPermissionDenied(){

        Toast.makeText(activity.getApplicationContext(),
                "Permission to read external storage has been denied", Toast.LENGTH_SHORT).show();
    }

    Song getSongFromId(int songID){

        Cursor cursor = activity.getApplicationContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] {
                        MediaStore.Audio.AudioColumns.TITLE,
                        MediaStore.Audio.AudioColumns.ARTIST,
                        MediaStore.Audio.AudioColumns.DURATION,
                        MediaStore.Images.ImageColumns._ID
                },
                MediaStore.Audio.AudioColumns._ID + "=?",
                new String[] { String.valueOf(songID) }, null);

        if(cursor!=null && cursor.moveToFirst())
            return new Song(songID,
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getInt(2),
                    cursor.getInt(3));

        return null;
    }

    ArrayList<Song> getAllSongs(){

        if(!checkPermissionForReadExternalStorage()){
            try {
                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_STORAGE_PERMISSION_REQUEST_CODE);

                if(!checkPermissionForReadExternalStorage()){
                    showPermissionDenied();
                    return null;
                }

            } catch (Exception e) {
                showPermissionDenied();
                return null;
            }
        }

        ArrayList<Song> foundSongs = new ArrayList<>();

        ContentResolver contentResolve = activity.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.AudioColumns._ID,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.Audio.AudioColumns.DURATION,
                MediaStore.Images.ImageColumns._ID
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        Cursor cursor = contentResolve.query(uri, projection, selection, null, null);

        if(cursor != null && cursor.moveToFirst()){

            do {

                foundSongs.add(new Song(cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getInt(4)));
            }
            while(cursor.moveToNext());
        }

        return foundSongs;
    }

}

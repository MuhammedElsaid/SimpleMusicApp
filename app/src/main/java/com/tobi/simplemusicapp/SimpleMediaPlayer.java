package com.tobi.simplemusicapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class SimpleMediaPlayer {

    private MediaPlayer mediaPlayer;
    private Activity activity;

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

    ArrayList<Song> getAllSongs(Activity activity){

        ArrayList<Song> foundSongs = new ArrayList<>();

        ContentResolver contentResolve = activity.getContentResolver();
        Cursor cursor = contentResolve.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);

        if(cursor != null && cursor.moveToFirst()){

            int titleId = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistId = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumId = cursor.getColumnIndex(android.provider.MediaStore.Audio.Albums.ALBUM_ART);
            int pathId = cursor .getColumnIndex(MediaStore.Audio.Media.DATA);

            do {
                String title = cursor.getString(titleId);
                String artistName = cursor.getString(artistId);
                String albumString = cursor.getString(albumId);
                String path = cursor.getString(pathId);

                foundSongs.add(new Song(path, title, artistName, albumString));
                //Bitmap albumArtBitmap = BitmapFactory.decodeFile();
            }
            while(cursor.moveToNext());
        }

        return foundSongs;
    }

}

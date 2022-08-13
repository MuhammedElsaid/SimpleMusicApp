package com.tobi.simplemusicapp;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;

public class Song {

    private String path;
    private String title;
    private String artist;
    private String albumArt;

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public Song(String path, String title, String artist, String albumArt) {
        this.path = path;
        this.title = title;
        this.artist = artist;
        this.albumArt = albumArt;
    }

    MediaPlayer getMediaPlayer(Activity activity){

        return MediaPlayer.create(activity.getApplicationContext(), Uri.parse(Uri.decode(path)));
    }
}

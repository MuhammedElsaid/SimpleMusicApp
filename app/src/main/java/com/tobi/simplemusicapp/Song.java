package com.tobi.simplemusicapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Size;

import java.io.IOException;

public class Song {

    public int id;
    private int duration;
    private String title;
    private String artist;
    private int albumArtId;

    public int getAlbumArtId() {
        return albumArtId;
    }

    public Uri getPath() {
        return ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    Bitmap getAlbumArt(ContentResolver contentResolver){

        Uri imgUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                albumArtId);
        try {
            return contentResolver.loadThumbnail(imgUri, new Size(300, 300), null);
        } catch (IOException ex) {
            return null;
        }
    }

    public Song(int id, String title, String artist, int duration, int albumArtId) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.albumArtId = albumArtId;
    }
}

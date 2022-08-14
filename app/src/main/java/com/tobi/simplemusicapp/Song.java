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

    private String path;
    private String title;
    private String artist;
    private int albumArtId;

    public int getAlbumArtId() {
        return albumArtId;
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public Bitmap getAlbumArt() {

        return null;
        //MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        //mmr.setDataSource(path);
        //byte [] data = mmr.getEmbeddedPicture();
        //return BitmapFactory.decodeByteArray(data, 0, data.length);
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

    public Song(String path, String title, String artist, int albumArtId) {
        this.path = path;
        this.title = title;
        this.artist = artist;
        this.albumArtId = albumArtId;
    }
}

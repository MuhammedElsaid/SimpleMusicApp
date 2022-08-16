package com.tobi.simplemusicapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MusicPlayerDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MusicPlayer.db";

    public MusicPlayerDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(MusicPlayFeedContract.MusicPlayerFeedEntry.SQL_CREATE_ENTRIES_PLAYLIST);
        db.execSQL(MusicPlayFeedContract.MusicPlayerFeedEntry.SQL_CREATE_ENTRIES_SONGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(MusicPlayFeedContract.MusicPlayerFeedEntry.SQL_DELETE_ENTRIES_PLAYLIST);
        db.execSQL(MusicPlayFeedContract.MusicPlayerFeedEntry.SQL_DELETE_ENTRIES_SONGS);
        onCreate(db);
    }


    public void deleteRow(String ID) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(MusicPlayFeedContract.MusicPlayerFeedEntry.TABLE_PLAYLIST, "PlayListID=?", new String[]{ID});
        db.delete(MusicPlayFeedContract.MusicPlayerFeedEntry.TABLE_SONGS, "PlayListID=?", new String[]{ID});

        db.close();
    }

    public boolean insertDataPlaylist(String Name) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(MusicPlayFeedContract.MusicPlayerFeedEntry.COLUMN_PLAYLIST_NAME,Name);

        return db.insert(MusicPlayFeedContract.MusicPlayerFeedEntry.TABLE_PLAYLIST,null, contentValues) == -1;
    }

    public int getLastId(){

        return (int)DatabaseUtils.longForQuery(getReadableDatabase(), "SELECT MAX("+ MusicPlayFeedContract.MusicPlayerFeedEntry._ID +") FROM " + MusicPlayFeedContract.MusicPlayerFeedEntry.TABLE_PLAYLIST, null);
    }

    public boolean insertDataSong(String songID, String PlaylistID) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(MusicPlayFeedContract.MusicPlayerFeedEntry.COLUMN_SONGS_PLAYLIST_ID, PlaylistID);
        contentValues.put(MusicPlayFeedContract.MusicPlayerFeedEntry.COLUMN_SONG_ID, songID);

        return db.insert(MusicPlayFeedContract.MusicPlayerFeedEntry.TABLE_SONGS,null, contentValues) == -1;
    }

    public ArrayList<Song> getSongsFromPlaylist(SimpleMediaPlayer simpleMediaPlayer, int playListId){

        SQLiteDatabase db = this.getReadableDatabase();
        String[] Projection = {
                MusicPlayFeedContract.MusicPlayerFeedEntry.COLUMN_SONGS_PLAYLIST_ID,
                MusicPlayFeedContract.MusicPlayerFeedEntry.COLUMN_SONG_ID
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = MusicPlayFeedContract.MusicPlayerFeedEntry.COLUMN_SONGS_PLAYLIST_ID + " = ?";
        String[] selectionArgs = { String.valueOf(playListId) };

        Cursor cursor = db.query(
                MusicPlayFeedContract.MusicPlayerFeedEntry.TABLE_SONGS,
                Projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        ArrayList<Song> songs = new ArrayList<Song>();

        while(cursor.moveToNext()) {

            int itemId = cursor.getInt(cursor.getColumnIndexOrThrow(MusicPlayFeedContract.MusicPlayerFeedEntry.COLUMN_SONG_ID));
            songs.add(simpleMediaPlayer.getSongFromId(itemId));
        }

        cursor.close();

        return songs;
    }

    public ArrayList<Playlist> getPlaylists(SimpleMediaPlayer simpleMediaPlayer){

        SQLiteDatabase db = this.getReadableDatabase();
        String[] Projection = {
                MusicPlayFeedContract.MusicPlayerFeedEntry._ID,
                MusicPlayFeedContract.MusicPlayerFeedEntry.COLUMN_PLAYLIST_NAME
        };

        Cursor cursor = db.query(
                MusicPlayFeedContract.MusicPlayerFeedEntry.TABLE_PLAYLIST,
                Projection,
                null,
                null,
                null,
                null,
                null
        );

        ArrayList<Playlist> playLists = new ArrayList<Playlist>();

        while(cursor.moveToNext()) {

            int playlistID = cursor.getInt(cursor.getColumnIndexOrThrow(MusicPlayFeedContract.MusicPlayerFeedEntry._ID));
            String playlistName = cursor.getString(cursor.getColumnIndexOrThrow(MusicPlayFeedContract.MusicPlayerFeedEntry.COLUMN_PLAYLIST_NAME));

            playLists.add(new Playlist(getSongsFromPlaylist(simpleMediaPlayer, playlistID), playlistName, playlistID));
        }

        cursor.close();


        return playLists;
    }

}

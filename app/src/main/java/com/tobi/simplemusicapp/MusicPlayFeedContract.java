package com.tobi.simplemusicapp;

import android.provider.BaseColumns;

public class MusicPlayFeedContract {

    public static class MusicPlayerFeedEntry implements BaseColumns {

        public static final String TABLE_PLAYLIST = "PlaylistTable";
        public static final String COLUMN_PLAYLIST_NAME = "PlaylistName";

        public static final String TABLE_SONGS = "SongsTable";
        public static final String COLUMN_SONGS_PLAYLIST_ID = "SongPlaylistID";
        public static final String COLUMN_SONG_ID = "SongID";

        public static final String SQL_CREATE_ENTRIES_PLAYLIST =
                "CREATE TABLE " + MusicPlayerFeedEntry.TABLE_PLAYLIST + " (" +
                        MusicPlayerFeedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        MusicPlayerFeedEntry.COLUMN_PLAYLIST_NAME + " TEXT)";

        public static final String SQL_CREATE_ENTRIES_SONGS =
                 "CREATE TABLE " + MusicPlayerFeedEntry.TABLE_SONGS + " (" +
                          MusicPlayerFeedEntry.COLUMN_SONGS_PLAYLIST_ID + " TEXT," +
                          MusicPlayerFeedEntry.COLUMN_SONG_ID + " TEXT)";

        public static final String SQL_DELETE_ENTRIES_PLAYLIST =
                "DROP TABLE IF EXISTS " + MusicPlayerFeedEntry.TABLE_PLAYLIST;

        public static final String SQL_DELETE_ENTRIES_SONGS =
                "DROP TABLE IF EXISTS " + MusicPlayerFeedEntry.TABLE_SONGS;
    }
}
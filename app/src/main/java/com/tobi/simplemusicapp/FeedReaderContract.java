package com.tobi.simplemusicapp;

import android.provider.BaseColumns;

public class FeedReaderContract {

    FeedReaderContract() {
    }
//converting SQL COMMAND TO STRINGS
//strings will be called in helper
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAMEn = "Pname";
        public static final String COLUMN_NAME_nID = "PlaylistID";
        public static final String COLUMN_NAME_n = "Name";

        public static final String TABLE_NAMEp = "Ppath";
        public static final String COLUMN_NAME_pID = "PlayListID";
        public static final String COLUMN_NAME_p = "Path";
//?
        public static final String SQL_CREATE_ENTRIESn =
                "CREATE TABLE " + FeedEntry.TABLE_NAMEn + " (" +
                        FeedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        FeedEntry.COLUMN_NAME_nID + " TEXT," +
                        FeedEntry.COLUMN_NAME_n + " TEXT)";

        public static final String SQL_CREATE_ENTRIESp =
                 "CREATE TABLE " + FeedEntry.TABLE_NAMEp + " (" +
                          FeedEntry._ID + " INTEGER," +
                          FeedEntry.COLUMN_NAME_pID + " TEXT," +
                          FeedEntry.COLUMN_NAME_p + " TEXT)";

        public static final String SQL_DELETE_ENTRIESn =
                "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAMEn;

        public static final String SQL_DELETE_ENTRIESp =
                "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAMEp;


    }
}
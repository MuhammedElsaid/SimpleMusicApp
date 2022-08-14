package com.tobi.simplemusicapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

//to manage DB
public class FeedReaderDbHelper extends SQLiteOpenHelper {
   //? public com.example.db.FeedReaderContract FD = new com.example.db.FeedReaderContract();
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MusicPlayer.db";
    public FeedReaderDbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


//creating tables
    @Override
    public void onCreate(SQLiteDatabase db){//,SQLiteDatabase db1) {
        db.execSQL(FeedReaderContract.FeedEntry.SQL_CREATE_ENTRIESn);
        db.execSQL(FeedReaderContract.FeedEntry.SQL_CREATE_ENTRIESp);

    }

    //deleting old db  and creating a new one
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){//,SQLiteDatabase db1, int oldVersion1, int newVersion1) {
        db.execSQL(FeedReaderContract.FeedEntry.SQL_DELETE_ENTRIESn);
        db.execSQL(FeedReaderContract.FeedEntry.SQL_DELETE_ENTRIESp);
        onCreate(db);

    }


    public void deleteRow(String ID) {

        // on below line we are creating
        // a variable to write our database./
        SQLiteDatabase db = this.getWritableDatabase();

       // delete
        db.delete(FeedReaderContract.FeedEntry.TABLE_NAMEn, "PlayListID=?", new String[]{ID});
        db.delete(FeedReaderContract.FeedEntry.TABLE_NAMEp, "PlayListID=?", new String[]{ID});

        db.close();
    }

    public boolean insertDataPlaylist(String Name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValuesn = new ContentValues();
        contentValuesn.put(FeedReaderContract.FeedEntry.COLUMN_NAME_n,Name);
        long resultn = db.insert(FeedReaderContract.FeedEntry.TABLE_NAMEn,null ,contentValuesn);



        if(resultn == -1)
            return false;
        else
            return true;
    }



    public boolean insertDataSong(String Path,String PlaylisID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValuesp = new ContentValues();
        contentValuesp.put(FeedReaderContract.FeedEntry.COLUMN_NAME_nID,PlaylisID);
        contentValuesp.put(FeedReaderContract.FeedEntry.COLUMN_NAME_n,Path);
        long resultp = db.insert(FeedReaderContract.FeedEntry.TABLE_NAMEp,null ,contentValuesp);



        if(resultp == -1)
            return false;
        else
            return true;
    }

}

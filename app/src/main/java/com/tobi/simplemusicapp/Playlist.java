package com.tobi.simplemusicapp;

import java.util.ArrayList;

public class Playlist extends ArrayList<Song> {

    private String title;
    private int ID;

    public Playlist(String title, int ID) {
        this.title = title;
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public int getID() {
        return ID;
    }
}

package com.tobi.simplemusicapp;

import java.util.ArrayList;

public class Playlist extends ArrayList<Song> {

    private String title;

    public Playlist(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}

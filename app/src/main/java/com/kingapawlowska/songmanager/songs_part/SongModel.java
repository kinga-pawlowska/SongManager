package com.kingapawlowska.songmanager.songs_part;

/**
 * Created by Kinga on 09.05.2018.
 */

public class SongModel {

    private String title;
    private String artist;

    public SongModel(String title, String author) {
        this.title = title;
        this.artist = author;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }
}

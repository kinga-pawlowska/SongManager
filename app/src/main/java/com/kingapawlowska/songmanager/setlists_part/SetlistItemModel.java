package com.kingapawlowska.songmanager.setlists_part;

/**
 * Created by Kinga on 01.05.2018.
 */

public class SetlistItemModel {
    String title;
    String artist;

    public SetlistItemModel(String title, String artist) {
        this.title = title;
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }
}

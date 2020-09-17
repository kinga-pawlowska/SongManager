package com.kingapawlowska.songmanager.songs_part;

import com.kingapawlowska.songmanager.Folders;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Kinga on 11.04.2018.
 */

public class SongsAddANewSong {
    private String title;
    private String artist;
    private String time;
    private String capo;
    private String tempo;

    public SongsAddANewSong(String title, String artist, String time, String capo, String tempo) {
        this.title = title;
        this.artist = artist;
        this.time = time;
        this.capo = capo;
        this.tempo = tempo;
    }

    public void createAFile() {
        Folders folders = new Folders();
        File newFile = new File(folders.getChildFolderSongs().toString(),
                "[" + this.artist + "]-[" + this.title + "]" + ".txt");
        try {
            FileWriter writer = new FileWriter(newFile);
            writer.append("{title: " + this.title + "}" + "\n");
            writer.append("{artist: " + this.artist + "}" + "\n");

            if(time.equals("")) {} else { writer.append("{time: " + this.time + "}" + "\n"); }
            if(capo.equals("")) {} else { writer.append("{capo: " + this.capo + "}" + "\n"); }
            if(tempo.equals("")) {} else { writer.append("{tempo: " + this.tempo + "}" + "\n"); }

            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

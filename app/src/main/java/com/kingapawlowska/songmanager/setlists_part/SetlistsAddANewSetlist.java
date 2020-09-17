package com.kingapawlowska.songmanager.setlists_part;

import com.kingapawlowska.songmanager.Folders;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Kinga on 11.04.2018.
 */

public class SetlistsAddANewSetlist {
    private String filename;

    public SetlistsAddANewSetlist(String filename) {
        this.filename = filename;
    }

    public void createAFile() {
        Folders folders = new Folders();
        File newFile = new File(folders.getChildFolderSetlists().toString(),
                "[" + this.filename + "]" + ".txt");
        try {
            FileWriter writer = new FileWriter(newFile);
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

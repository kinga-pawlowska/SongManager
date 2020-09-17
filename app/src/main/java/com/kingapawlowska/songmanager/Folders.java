package com.kingapawlowska.songmanager;

import android.os.Environment;

import java.io.File;

/**
 * Created by Kinga on 16.01.2018.
 */

public class Folders {

    private File mainFolder;
    private File childFolderSongs;
    private File childFolderSetlists;
    private File childFolderGigs;

    public Folders() {
        mainFolder = new File(Environment.getExternalStorageDirectory() + "/SongManager");
        childFolderSongs = new File(Environment.getExternalStorageDirectory() + "/SongManager" + "/Songs");
        childFolderSetlists = new File(Environment.getExternalStorageDirectory() + "/SongManager" + "/Setlists");
        childFolderGigs = new File(Environment.getExternalStorageDirectory() + "/SongManager" + "/Gigs");
    }

    public boolean checkIfFoldersExists(){

        if ((!mainFolder.exists() && !mainFolder.isDirectory()) ||
                (!childFolderSongs.exists() && !childFolderSongs.isDirectory()) ||
                (!childFolderSetlists.exists() && !childFolderSetlists.isDirectory()) ||
                (!childFolderGigs.exists() && !childFolderGigs.isDirectory())
                ) {
            return false;
        }
        else {
            return true;
        }
    }

    public void createFolders() {
        mainFolder.mkdir();
        childFolderSongs.mkdir();
        childFolderSetlists.mkdir();
        childFolderGigs.mkdir();
    }

    public File getMainFolder() {
        return mainFolder;
    }

    public File getChildFolderSongs() {
        return childFolderSongs;
    }

    public File getChildFolderSetlists() {
        return childFolderSetlists;
    }

    public File getChildFolderGigs() {
        return childFolderGigs;
    }
}
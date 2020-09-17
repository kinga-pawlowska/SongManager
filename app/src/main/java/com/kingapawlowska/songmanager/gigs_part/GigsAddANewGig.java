package com.kingapawlowska.songmanager.gigs_part;

import com.kingapawlowska.songmanager.Folders;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Kinga on 11.04.2018.
 */

public class GigsAddANewGig {

    private int day;
    private int month;
    private int year;
    private String venue;
    private String name;
    private String filename;

    public GigsAddANewGig(int day, int month, int year, String venue, String name, String filename) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.venue = venue;
        this.name = name;
        this.filename = filename;
    }

    public void createAFile() {
        Folders folders = new Folders();
        File newFile = new File(folders.getChildFolderGigs().toString(),
                "[" + this.filename + "]" + ".txt");
        try {
            FileWriter writer = new FileWriter(newFile);

            String strDay = "{day: " + this.day + "};" + "\n";
            writer.append(strDay);
            String strMonth = "{month: " + this.month + "};" + "\n";
            writer.append(strMonth);
            String strYear = "{year: " + this.year + "};" + "\n";
            writer.append(strYear);
            String strVenue = "{venue: " + this.venue + "};" + "\n";
            writer.append(strVenue);
            String strName = "{name: " + this.name + "};" + "\n";
            writer.append(strName);

            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

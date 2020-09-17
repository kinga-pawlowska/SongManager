package com.kingapawlowska.songmanager.gigs_part;

import java.io.StringReader;

/**
 * Created by Kinga on 10.05.2018.
 */

public class GigModel {

    private int day;
    private int month;
    private int year;

    private String venue;
    private String name;

    public GigModel(int day, int month, int year, String venue, String name) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.venue = venue;
        this.name = name;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public String getVenue() {
        return venue;
    }

    public String getName() {
        return name;
    }
}

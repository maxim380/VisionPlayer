package com.maxim.visionplayer.Models;

/**
 * Created by maxim on 4/8/2018.
 */

public class UserFriend {

    private int id;
    private String currentSong;
    private String locationLong;
    private String locationLat;
    private String name;

    public UserFriend(int id, String currentSong, String locationLat, String locationLong, String name) {
        this.id = id;
        this.currentSong = currentSong;
        this.locationLat = locationLat;
        this.locationLong = locationLong;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getCurrentSong() {
        return currentSong;
    }

    public String getLocationLong() {
        return locationLong;
    }

    public String getLocationLat() {
        return locationLat;
    }

    public String getName() {
        return name;
    }
}

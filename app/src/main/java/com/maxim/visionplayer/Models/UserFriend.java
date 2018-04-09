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
    private String currentSongArtist;

    public UserFriend(int id, String currentSong, String locationLat, String locationLong, String name, String currentSongArtist) {
        this.id = id;
        this.currentSong = currentSong;
        this.locationLat = locationLat;
        this.locationLong = locationLong;
        this.name = name;
        this.currentSong = currentSong;

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

    public String getCurrentSongArtist() {
        return currentSongArtist;
    }
}

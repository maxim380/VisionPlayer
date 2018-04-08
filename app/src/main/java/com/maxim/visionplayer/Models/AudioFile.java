package com.maxim.visionplayer.Models;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by maxim on 3/17/2018.
 */

public class AudioFile implements Serializable {

    private String data;
    private String title;
    private String album;
    private String artist;
    private Bitmap albumArt;

    public AudioFile(String data, String title, String album, String artist, Bitmap albumArt) {
        this.data = data;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.albumArt = albumArt;
    }

    public String getData() {
        return data;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }


}

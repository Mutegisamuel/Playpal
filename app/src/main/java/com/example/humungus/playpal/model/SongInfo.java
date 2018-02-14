package com.example.humungus.playpal.model;

/**
 * Created by humungus on 2/13/18.
 */

public class SongInfo {
    public String image ,songName ,artistName, songUrl;

    public SongInfo() {
    }

    public SongInfo(String image, String songName, String artistName, String songUrl) {
        this.image = image;
        this.songName = songName;
        this.artistName = artistName;
        this.songUrl = songUrl;
    }


}

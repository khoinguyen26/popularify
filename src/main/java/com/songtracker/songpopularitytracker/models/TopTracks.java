package com.songtracker.songpopularitytracker.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "top_tracks")
public class TopTracks {
    @Id
    private String songId;
    private String name;
    private String artist;
    private String album;
    private String albumImage;
    private String uri;
    private int popularity;

    private String userId;

    public TopTracks() {
    }

    public TopTracks(String songId, String name, String artist, String album, String albumImage, String uri, int popularity, String userId) {
        this.songId = songId;
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.albumImage = albumImage;
        this.popularity = popularity;
        this.uri = uri;
        this.userId = userId;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getAlbumImage() {
        return albumImage;
    }

    public void setAlbumImage(String albumImage) {
        this.albumImage = albumImage;
    }


    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

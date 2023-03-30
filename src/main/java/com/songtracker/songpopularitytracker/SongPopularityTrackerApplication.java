package com.songtracker.songpopularitytracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;


import java.net.URI;

@SpringBootApplication
public class SongPopularityTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SongPopularityTrackerApplication.class, args);
    }

}

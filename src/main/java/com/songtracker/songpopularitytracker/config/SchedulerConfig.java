package com.songtracker.songpopularitytracker.config;

import com.songtracker.songpopularitytracker.services.TopTracksService;
import com.songtracker.songpopularitytracker.services.UserService;
import org.apache.hc.core5.http.ParseException;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;

@Configuration
@EnableScheduling
public class SchedulerConfig {
    private TopTracksService topTracksService;
    private UserService userService;

    public SchedulerConfig(TopTracksService topTracksService, UserService userService) {
        this.topTracksService = topTracksService;
        this.userService = userService;
    }


    // run every 24 hours
    @Scheduled(fixedRate = 86400000)
    public void scheduleFixedRateTask() throws IOException, ParseException, SpotifyWebApiException {
        topTracksService.updateTopTracksPopularityPeriod();
    }

    // refresh access token every 50 minutes
    @Scheduled(fixedRate = 3000000)
    public void scheduleFixedRateTask2() throws IOException, ParseException, SpotifyWebApiException {
        userService.refreshAccessToken();
    }
}

package com.songtracker.songpopularitytracker.controllers;

import com.songtracker.songpopularitytracker.DTO.ApiResponse;
import com.songtracker.songpopularitytracker.models.Song;
import com.songtracker.songpopularitytracker.models.TopTracks;
import com.songtracker.songpopularitytracker.models.User;
import com.songtracker.songpopularitytracker.services.*;
import com.songtracker.songpopularitytracker.utils.TopTrackType;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopTracksRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/users")
public class UserController {


    private TopTracksService topTracksService;
    private SpotifyService spotifyService;


    @Autowired
    public UserController(TopTracksService topTracksService, SpotifyService spotifyService) {
        this.topTracksService = topTracksService;
        this.spotifyService = spotifyService;
    }

    @GetMapping("/top-tracks")
    @Operation(summary = "Get user's top tracks")
    public ResponseEntity<ApiResponse<List<TopTracks>>> getUserTop() {
        try {
            CompletableFuture<List<TopTracks>> futureTopTracks = spotifyService.getUserTopTracksAsync();
            List<TopTracks> topTracks = futureTopTracks.get();
            for (TopTracks topTrack : topTracks) {
                topTracksService.saveTopTracks(topTrack);
            }
            ApiResponse<List<TopTracks>> response = new ApiResponse<>("User's top tracks fetched successfully.", topTracks);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

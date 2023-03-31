package com.songtracker.songpopularitytracker.controllers;

import com.songtracker.songpopularitytracker.DTO.ApiResponse;
import com.songtracker.songpopularitytracker.models.Song;
import com.songtracker.songpopularitytracker.models.TopTracks;
import com.songtracker.songpopularitytracker.models.User;
import com.songtracker.songpopularitytracker.services.SequenceService;
import com.songtracker.songpopularitytracker.services.SongService;
import com.songtracker.songpopularitytracker.services.TopTracksService;
import com.songtracker.songpopularitytracker.services.UserService;
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

    private SpotifyApi spotifyApi;
    private TopTracksService topTracksService;
    private UserService userService;
    private SequenceService sequenceService;

    @Autowired
    public UserController(SpotifyApi spotifyApi, SequenceService sequenceService, UserService userService, TopTracksService topTracksService) {
        this.spotifyApi = spotifyApi;
        this.sequenceService = sequenceService;
        this.userService = userService;
        this.topTracksService = topTracksService;
    }

    @GetMapping("/top-tracks")
    @Operation(summary = "Get user's top tracks")
    public ResponseEntity<ApiResponse<List<TopTracks>>> getUserTop() {
        List<TopTracks> songs = new ArrayList<>();
        AtomicReference<String> userId = new AtomicReference<>("");
        ApiResponse<List<TopTracks>> response = new ApiResponse<>();

        try {
            userId.set(spotifyApi.getCurrentUsersProfile().build().execute().getId());
            GetUsersTopTracksRequest getUsersTopTracksRequest = spotifyApi.getUsersTopTracks()
                    .build();
            Paging<Track> trackPaging = getUsersTopTracksRequest.execute();
            Track[] tracks = trackPaging.getItems();

            List<CompletableFuture<TopTracks>> futures = new ArrayList<>();
            for (Track track : tracks) {
                CompletableFuture<TopTracks> topTracks = CompletableFuture.supplyAsync(() -> {
                    TopTracks topTrack = new TopTracks();
                    topTrack.setSongId(track.getId());
                    topTrack.setName(track.getName());
                    topTrack.setArtist(track.getArtists()[0].getName());
                    topTrack.setPopularity(track.getPopularity());
                    topTrack.setUri(track.getUri());
                    topTrack.setAlbum(track.getAlbum().getName());
                    topTrack.setAlbumImage(track.getAlbum().getImages()[0].getUrl());
                    topTrack.setUserId(userId.get());

                    return topTrack;
                });
                futures.add(topTracks);
            }
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
            allFutures.get();

            for (CompletableFuture<TopTracks> future : futures) {
                TopTracks topTrack = future.get();
                // check if top track exists
                if (topTrack != null) {
                    songs.add(topTrack);
                }
            }

            // save top tracks
            for (TopTracks song : songs) {
                topTracksService.saveTopTracks(song);
            }

            response.setMessage("Top tracks retrieved successfully");
            response.setData(songs);

            return ResponseEntity.ok(response);
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
            return null;
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

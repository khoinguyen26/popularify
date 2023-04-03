package com.songtracker.songpopularitytracker.services;

import com.songtracker.songpopularitytracker.models.TopTracks;
import com.songtracker.songpopularitytracker.repository.SpotifyServiceInterface;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
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

@Service
public class SpotifyService implements SpotifyServiceInterface {
    private final SpotifyApi spotifyApi;


    @Autowired
    public SpotifyService(SpotifyApi spotifyApi) {
        this.spotifyApi = spotifyApi;

    }

    @Override
    @Async
    public CompletableFuture<List<TopTracks>> getUserTopTracksAsync() {
        AtomicReference<String> userId = new AtomicReference<>("");
        List<CompletableFuture<TopTracks>> futures = new ArrayList<>();
        try {
            userId.set(spotifyApi.getCurrentUsersProfile().build().execute().getId());
            GetUsersTopTracksRequest getUsersTopTracksRequest = spotifyApi.getUsersTopTracks()
                    .build();
            Paging<Track> trackPaging = getUsersTopTracksRequest.execute();
            Track[] tracks = trackPaging.getItems();


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

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (SpotifyWebApiException e) {
            throw new RuntimeException(e);
        }

        return CompletableFuture.supplyAsync(() -> {
            List<TopTracks> topTracksList = new ArrayList<>();
            for (CompletableFuture<TopTracks> future : futures) {
                try {
                    topTracksList.add(future.get());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
            return topTracksList;
        });
    }
}

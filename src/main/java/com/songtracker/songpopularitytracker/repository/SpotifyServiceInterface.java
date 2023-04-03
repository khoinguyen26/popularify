package com.songtracker.songpopularitytracker.repository;

import com.songtracker.songpopularitytracker.models.TopTracks;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Repository
public interface SpotifyServiceInterface {
    CompletableFuture<List<TopTracks>> getUserTopTracksAsync() throws Exception;
}

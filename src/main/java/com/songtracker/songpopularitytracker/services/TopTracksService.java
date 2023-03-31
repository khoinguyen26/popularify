package com.songtracker.songpopularitytracker.services;

import com.songtracker.songpopularitytracker.models.TopTracks;
import com.songtracker.songpopularitytracker.repository.TopTracksRepository;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopTracksRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TopTracksService {
    private final TopTracksRepository topTracksRepository;

    private SpotifyApi spotifyApi;
    private Environment env;

    @Autowired
    public TopTracksService(TopTracksRepository topTracksRepository, SpotifyApi spotifyApi, Environment env) {
        this.topTracksRepository = topTracksRepository;
        this.spotifyApi = spotifyApi;
        this.env = env;
        spotifyApi.setAccessToken(env.getProperty("spotify.access-token"));
    }

    public TopTracks saveTopTracks(TopTracks topTracks) {
        if (existsById(topTracks.getSongId())) {
            return updateTopTracksPopularity(topTracks);
        }
        return topTracksRepository.save(topTracks);
    }


    // check if top tracks exists
    public boolean existsById(String songId) {
        return topTracksRepository.existsById(songId);
    }

    // update existing top tracks
    public TopTracks updateTopTracksPopularity(TopTracks topTracks) {
        // update top tracks
        TopTracks existingTopTracks = topTracksRepository.findById(topTracks.getSongId()).orElse(null);
        if (existingTopTracks != null) {
            existingTopTracks.setPopularity(topTracks.getPopularity());
            return topTracksRepository.save(existingTopTracks);
        }
        return null;
    }


    public void updateTopTracksPopularityPeriod() throws IOException, ParseException, SpotifyWebApiException {
        List<TopTracks> currentTopTracks = topTracksRepository.findAll();
        List<TopTracks> updatedTopTracks = getTopTracksFromSpotify();

        for (TopTracks currentTopTrack : currentTopTracks) {
            for (TopTracks updatedTopTrack : updatedTopTracks) {
                if (currentTopTrack.getSongId().equals(updatedTopTrack.getSongId())) {
                    currentTopTrack.setPopularity(updatedTopTrack.getPopularity());
                    topTracksRepository.save(currentTopTrack);
                }
            }
        }
    }

    // get top tracks from spotify
    public List<TopTracks> getTopTracksFromSpotify() throws IOException, ParseException, SpotifyWebApiException {
        GetUsersTopTracksRequest getUsersTopTracksRequest = spotifyApi.getUsersTopTracks()
                .build();

        Paging<Track> trackPaging = getUsersTopTracksRequest.execute();
        Track[] tracks = trackPaging.getItems();
        List<TopTracks> topTracks = new ArrayList<>();

        for (Track track : tracks) {
            TopTracks topTrack = new TopTracks();
            topTrack.setSongId(track.getId());
            topTrack.setName(track.getName());
            topTrack.setArtist(track.getArtists()[0].getName());
            topTrack.setAlbum(track.getAlbum().getName());
            topTrack.setAlbumImage(track.getAlbum().getImages()[0].getUrl());
            topTrack.setUri(track.getUri());
            topTrack.setPopularity(track.getPopularity());
            topTracks.add(topTrack);
        }

        return topTracks;
    }
}

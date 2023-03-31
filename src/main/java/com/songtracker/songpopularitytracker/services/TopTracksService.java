package com.songtracker.songpopularitytracker.services;

import com.songtracker.songpopularitytracker.models.TopTracks;
import com.songtracker.songpopularitytracker.repository.TopTracksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopTracksService {
    private final TopTracksRepository topTracksRepository;

    @Autowired
    public TopTracksService(TopTracksRepository topTracksRepository) {
        this.topTracksRepository = topTracksRepository;
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

}

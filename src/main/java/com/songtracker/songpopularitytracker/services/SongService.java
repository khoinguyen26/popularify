package com.songtracker.songpopularitytracker.services;

import com.songtracker.songpopularitytracker.models.Song;
import com.songtracker.songpopularitytracker.repository.SpotifyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SongService {
    private SpotifyRepository spotifyRepository;

    @Autowired
    public SongService(SpotifyRepository spotifyRepository) {
        this.spotifyRepository = spotifyRepository;
    }

    public Song saveSong(Song song) {
        return spotifyRepository.save(song);
    }

    public List<Song> getAllSongs() {
        return spotifyRepository.findAll();
    }

    public Song getSongById(String id) {
        return spotifyRepository.findById(id).orElse(null);
    }

    public List<Song> getSongByName(String name) {
        return spotifyRepository.findByName(name);
    }
}

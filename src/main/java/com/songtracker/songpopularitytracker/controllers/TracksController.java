package com.songtracker.songpopularitytracker.controllers;

import com.songtracker.songpopularitytracker.models.Song;
import com.songtracker.songpopularitytracker.repository.TrackRepository;
import com.songtracker.songpopularitytracker.services.SongService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tracks")
@Tag(name = "Tracks", description = "API to get tracks info")
public class TracksController {
    private SongService songService;

    @Autowired
    public TracksController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping("/all")
    public List<Song> getTracks() {
        return songService.getAllSongs();
    }
}

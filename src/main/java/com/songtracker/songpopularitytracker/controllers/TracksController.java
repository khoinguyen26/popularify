package com.songtracker.songpopularitytracker.controllers;

import com.songtracker.songpopularitytracker.services.SongService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.michaelthelin.spotify.SpotifyApi;

@RestController
@RequestMapping("/tracks")
@Tag(name = "Tracks", description = "API to get tracks info")
public class TracksController {

}

package com.songtracker.songpopularitytracker.repository;

import com.songtracker.songpopularitytracker.models.Song;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TrackRepository extends MongoRepository<Song, String> {
}

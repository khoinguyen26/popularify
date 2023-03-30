package com.songtracker.songpopularitytracker.repository;

import com.songtracker.songpopularitytracker.models.Song;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpotifyRepository extends MongoRepository<Song, String> {
    List<Song> findByName(String name);
}

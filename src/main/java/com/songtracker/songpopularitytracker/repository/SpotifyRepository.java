package com.songtracker.songpopularitytracker.repository;

import com.songtracker.songpopularitytracker.utils.TopTrackType;
import com.songtracker.songpopularitytracker.models.Song;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpotifyRepository extends MongoRepository<Song, String> {

    @Query("{ 'name' : ?0 }")
    List<Song> findByName(String name);
}

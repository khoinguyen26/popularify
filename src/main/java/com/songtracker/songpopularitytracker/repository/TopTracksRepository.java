package com.songtracker.songpopularitytracker.repository;

import com.songtracker.songpopularitytracker.models.TopTracks;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopTracksRepository extends MongoRepository<TopTracks, String> {
}

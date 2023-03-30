package com.songtracker.songpopularitytracker.repository;

import com.songtracker.songpopularitytracker.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
}

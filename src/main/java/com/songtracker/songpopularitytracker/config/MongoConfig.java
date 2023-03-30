package com.songtracker.songpopularitytracker.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Objects;

@Configuration
public class MongoConfig {
    private final Environment env;

    @Autowired
    public MongoConfig(Environment env) {
        this.env = env;
    }

    // connect to MongoDB Atlas
    @Bean
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(
                Objects.requireNonNull(env.getProperty("spring.data.mongodb.uri"))
        );
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder().version(ServerApiVersion.V1).build())
                .build();
        MongoClient mongoClient = MongoClients.create(mongoClientSettings);
        MongoDatabase database = mongoClient.getDatabase("songTracker");
        return mongoClient;
    }



}

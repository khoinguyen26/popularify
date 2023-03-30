package com.songtracker.songpopularitytracker.models;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "database_sequence")
public class Sequence {
    private String id;
    private long seq;

    public Sequence() {
    }

    public Sequence(String id, long seq) {
        this.id = id;
        this.seq = seq;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }
}


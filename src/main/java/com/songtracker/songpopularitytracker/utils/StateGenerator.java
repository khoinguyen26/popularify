package com.songtracker.songpopularitytracker.utils;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class StateGenerator {
    public String generateState() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] stateBytes = new byte[32];
        secureRandom.nextBytes(stateBytes);
        return Base64.getUrlEncoder().encodeToString(stateBytes);
    }
}

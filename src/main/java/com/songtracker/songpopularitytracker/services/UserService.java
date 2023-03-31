package com.songtracker.songpopularitytracker.services;

import com.songtracker.songpopularitytracker.models.User;
import com.songtracker.songpopularitytracker.repository.UserRepository;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import java.io.IOException;
import java.net.URI;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class UserService {
    private final UserRepository userRepository;
    private SpotifyApi spotifyApi;
    private String state;
    private Environment env;
    @Autowired
    public UserService(UserRepository userRepository, SpotifyApi spotifyApi, Environment env) {
        this.userRepository = userRepository;
        this.spotifyApi = spotifyApi;
        this.env = env;
        spotifyApi.setAccessToken(env.getProperty("spotify.access-token"));
        spotifyApi.setRefreshToken(env.getProperty("spotify.refresh-token"));
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }


    public boolean existsById(String spotifyId) {
        return userRepository.existsById(spotifyId);
    }

    // get user by id
    public User getUserById(String spotifyId) {
        return userRepository.findById(spotifyId).orElse(null);
    }

    public URI authorizeUri() {
        this.state = generateState();
        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                .state(state)
                .scope("user-read-private user-read-email user-top-read")
                .show_dialog(true)
                .build();
        URI uri = authorizationCodeUriRequest.execute();

        return uri;
    }

    private String generateState() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] stateBytes = new byte[32];
        secureRandom.nextBytes(stateBytes);
        return Base64.getUrlEncoder().encodeToString(stateBytes);
    }

    public String getState() {
        return state;
    }


    public AuthorizationCodeCredentials getAccessAndRefreshTokens(String code) {
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code).build();
        AuthorizationCodeCredentials authorizationCodeCredentials = null;
        try {
            authorizationCodeCredentials = authorizationCodeRequest.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
        spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

        return authorizationCodeCredentials;
    }


    public AuthorizationCodeCredentials refreshAccessToken() throws IOException, ParseException, SpotifyWebApiException {
        AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh().build();
        AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());


        return authorizationCodeCredentials;
    }
}

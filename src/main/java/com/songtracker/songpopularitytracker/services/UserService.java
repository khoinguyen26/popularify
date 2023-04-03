package com.songtracker.songpopularitytracker.services;

import com.songtracker.songpopularitytracker.models.User;
import com.songtracker.songpopularitytracker.repository.UserRepository;
import com.songtracker.songpopularitytracker.utils.StateGenerator;
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

    private StateGenerator stateGenerator;

    @Autowired
    public UserService(UserRepository userRepository, SpotifyApi spotifyApi, Environment env, StateGenerator stateGenerator) {
        this.userRepository = userRepository;
        this.spotifyApi = spotifyApi;
        this.env = env;
        this.stateGenerator = stateGenerator;
        spotifyApi.setAccessToken(env.getProperty("spotify.access-token"));
        spotifyApi.setRefreshToken(env.getProperty("spotify.refresh-token"));
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    // get current user
    public User getCurrentUser() throws IOException, ParseException, SpotifyWebApiException {
        User user = new User();
        user.setId(spotifyApi.getCurrentUsersProfile().build().execute().getId());
        user.setName(spotifyApi.getCurrentUsersProfile().build().execute().getDisplayName());
        user.setEmail(spotifyApi.getCurrentUsersProfile().build().execute().getEmail());
        user.setAccessToken(spotifyApi.getAccessToken());
        user.setRefreshToken(spotifyApi.getRefreshToken());
        return user;
    }

    public boolean existsById(String id) {
        return userRepository.existsById(id);
    }


    // get user access and refresh tokens from database and set them to spotifyApi
    public void setAccessAndRefreshTokens(User user) {
        spotifyApi.setAccessToken(user.getAccessToken());
        spotifyApi.setRefreshToken(user.getRefreshToken());
    }


    public URI authorizeUri() {
        this.state = stateGenerator.generateState();
        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                .state(state)
                .scope("user-read-private user-read-email user-top-read")
                .show_dialog(true)
                .build();
        URI uri = authorizationCodeUriRequest.execute();

        return uri;
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
        User user = getCurrentUser();
        setAccessAndRefreshTokens(user);
        AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh().build();
        AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();
        spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());


        return authorizationCodeCredentials;
    }
}

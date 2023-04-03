package com.songtracker.songpopularitytracker.controllers;

import com.songtracker.songpopularitytracker.models.User;
import com.songtracker.songpopularitytracker.services.SequenceService;
import com.songtracker.songpopularitytracker.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

@RestController
@RequestMapping("/auth")
@Tag(name = "Spotify Auth", description = "Spotify Auth API")
public class SpotifyAuthController {


    private UserService userService;


    @Autowired
    public SpotifyAuthController(UserService userService) {
        this.userService = userService;

    }

    @GetMapping("/login")
    public String login(HttpServletResponse response) throws IOException {
        URI authorizationUri = userService.authorizeUri();
        if (authorizationUri != null && authorizationUri.toString().length() > 0) {
            response.sendRedirect(authorizationUri.toString());
            return authorizationUri.toString();
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return "redirect:/";
        }
    }


    @GetMapping("/access-tkn")
    public ResponseEntity<String> callback(@RequestParam("code") String code, @RequestParam("state") String state) throws IOException, ParseException, SpotifyWebApiException {
        String stateFromServer = userService.getState();
        if (stateFromServer == null || !stateFromServer.equals(state)) {
            return ResponseEntity.badRequest().body("State value did not match");
        }

        AuthorizationCodeCredentials authorizationCodeCredentials = userService.getAccessAndRefreshTokens(code);
        if (authorizationCodeCredentials == null) {
            return ResponseEntity.badRequest().body("Error getting access token");
        }


        return ResponseEntity.ok("Access token: " + authorizationCodeCredentials.getAccessToken() + " Refresh token: " + authorizationCodeCredentials.getRefreshToken());
    }


    // save current user
    @PostMapping("/save")
    @Operation(summary = "Save current user", description = "Save current user to database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User saved",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = User.class)))}),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "409", description = "User already exists", content = @Content)})
    public ResponseEntity<User> saveUser() throws IOException, ParseException, SpotifyWebApiException {
        User user = null;

        user = userService.getCurrentUser();
        // check if user already exists
        if (userService.existsById(user.getId()))
            userService.setAccessAndRefreshTokens(user);
        else
            userService.saveUser(user);

        return ResponseEntity.ok(user);
    }

    // refresh access token
    @GetMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Refresh access token")
    public ResponseEntity<String> refreshAccessToken() throws IOException, ParseException, SpotifyWebApiException {
        AuthorizationCodeCredentials authorizationCodeCredentials = userService.refreshAccessToken();
        if (authorizationCodeCredentials == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok("Access token refreshed");
    }


}

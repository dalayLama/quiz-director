package org.quizstorage.director.controllers;

import lombok.RequiredArgsConstructor;
import org.quizstorage.director.components.UserService;
import org.quizstorage.director.configurations.paths.ApiPaths;
import org.quizstorage.director.services.UserWebSocketTokenDataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class WebSocketTokenController {

    private final UserWebSocketTokenDataService tokenDataService;

    private final UserService userService;

    @HeadersAuth
    @GetMapping(ApiPaths.WEB_SOCKET_TOKEN_V1)
    public UUID generateToken() {
        return userService.doAndReturnAsCurrentUser(user -> tokenDataService.generateToken(user).getTokenId());
    }

}

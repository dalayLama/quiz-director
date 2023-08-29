package org.quizstorage.director.configurations;

import org.springframework.boot.autoconfigure.service.connection.ConnectionDetails;

public record WebSocketConnectionDetails(
        int relayPort,

        String relayHost,

        String systemLogin,

        String systemPasscode,
        String clientLogin,
        String clientPasscode
) implements ConnectionDetails {}

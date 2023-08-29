package org.quizstorage.director.events;

import org.quizstoradge.director.dto.GameEventType;
import org.quizstoradge.director.dto.GameInfo;
import org.quizstoradge.director.dto.GameResult;

public record WebSocketGameEvent(
        GameInfo gameInfo,
        GameEventType eventType,
        GameResult gameResult
) {

    public WebSocketGameEvent(GameInfo gameInfo, GameResult gameResult) {
        this(gameInfo, GameEventType.FINISHED_GAME, gameResult);
    }

    public WebSocketGameEvent(GameInfo gameInfo, GameEventType eventType) {
        this(gameInfo, eventType, null);
    }

}

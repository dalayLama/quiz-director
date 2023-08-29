package org.quizstorage.director.configurations.paths;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ApiPaths {

    public static final String API = "/api";

    public static final String API_V1 = API + "/v1";

    public static final String GAMES_V1 = API_V1 + "/games";

    public static final String CURRENT_QUESTION_V1 = API_V1 + "/current-game/current-question";

    public static final String ACCEPT_ANSWER_V1 = GAMES_V1 + "/{gameId}/questions/{questionNumber}/answers";
    public static final String FINISHED_GAMES_V1 = API_V1 + "/finished-games";

    public static final String GAME_RESULT_V1 = GAMES_V1 + "/{gameId}/result";

    public static final String WEB_SOCKET_TOKEN_V1 = API_V1 + "/websocket/token";
}

package org.quizstorage.director.configurations.paths;

import lombok.experimental.UtilityClass;

@UtilityClass
public class WebSocketDestinations {

    public static final String CURRENT_QUESTION = "/currentQuestion";

    public static final String GAMES_EVENTS = "/games.events";

    public static final String SEND_CURRENT_QUESTION = "/topic/commands.sendCurrentQuestion";

    public static final String PROCESS_ANSWER = "/topic/commands.processAnswer";


}

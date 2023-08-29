package org.quizstorage.director.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.quizstorage.director.configurations.paths.WebSocketDestinations;
import org.quizstorage.director.events.WebSocketQuizGameNotifier;
import org.quizstorage.director.services.GameDirector;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Set;

@Controller
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "websocket", name = "enable", havingValue = "true")
public class WsController {

    private final GameDirector gameDirector;

    private final WebSocketQuizGameNotifier gameNotifier;

    @MessageMapping(WebSocketDestinations.PROCESS_ANSWER)
    public void processAnswer(Principal principal, @Payload ProcessingAnswersRequest request) {
        log.info("Received answers from {} for the question {} of game {}",
                principal.getName(),
                request.questionNumber(),
                request.gameId());
        gameDirector.acceptAnswers(new ObjectId(request.gameId()), request.questionNumber(), request.answers());
    }

    @MessageMapping(WebSocketDestinations.SEND_CURRENT_QUESTION)
    public void sendCurrentQuestion(Principal principal) {
        log.info("Received a request from {} to send a current question to WebSocket channel", principal.getName());
        gameDirector.getCurrentQuestion().ifPresent(gameNotifier::sendCurrentQuestion);
    }

    public record ProcessingAnswersRequest(
            String gameId,
            int questionNumber,
            Set<String> answers
    ) {}

}

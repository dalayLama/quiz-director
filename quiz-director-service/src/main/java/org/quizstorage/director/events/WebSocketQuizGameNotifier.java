package org.quizstorage.director.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quizstoradge.director.dto.AnswerResult;
import org.quizstoradge.director.dto.GameQuestionDto;
import org.quizstorage.director.configurations.paths.WebSocketDestinations;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(prefix = "websocket", name = "enable", havingValue = "true")
public class WebSocketQuizGameNotifier {

    private final SimpMessageSendingOperations operations;

    @EventListener(classes = GameEvent.class)
    public void onGameEvent(@NonNull GameEvent event) {
        log.info("handle game event - {}", event);
        WebSocketGameEvent webSocketGameEvent = Optional.ofNullable(event.getQuizGameAnalysis())
                .map(analysis -> new WebSocketGameEvent(analysis.gameInfo(), analysis.gameResult()))
                .orElseGet(() -> new WebSocketGameEvent(event.getGameInfo(), event.getEventType()));
        operations.convertAndSendToUser(
                event.getGameInfo().userId(), WebSocketDestinations.GAMES_EVENTS, webSocketGameEvent);
    }

    @EventListener(classes = QuestionEvent.class)
    public void onQuestionEvent(@NonNull QuestionEvent event) {
        log.info("handle question event - {}", event);
        Optional.ofNullable(event.getAnswerResult())
                .map(AnswerResult::nextQuestion)
                .ifPresent(this::sendCurrentQuestion);
    }

    public void sendCurrentQuestion(GameQuestionDto gameQuestion) {
        log.info("send current question - {}", gameQuestion);
        String dest = WebSocketDestinations.CURRENT_QUESTION;
        operations.convertAndSendToUser(gameQuestion.gameInfo().userId(), dest, gameQuestion);
    }

}

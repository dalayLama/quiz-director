package org.quizstorage.director.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.quizstoradge.director.dto.AnswerResult;
import org.quizstoradge.director.dto.QuizGameAnalysis;
import org.quizstoradge.director.dto.GameEventType;
import org.quizstoradge.director.dto.GameInfo;
import org.quizstorage.director.components.GameAnalyzer;
import org.quizstorage.director.services.QuizGameService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultGameNotifier implements GameNotifier {

    private final ApplicationEventPublisher eventPublisher;

    private final GameAnalyzer gameAnalyzer;

    private final QuizGameService gameService;

    @Override
    @Async
    public void notifyStartGame(GameInfo gameInfo) {
        eventPublisher.publishEvent(new GameEvent(this, gameInfo, GameEventType.STARTED_GAME));
    }

    @Override
    @Async
    public void notifyEndGame(GameInfo gameInfo) {
        gameService.findById(new ObjectId(gameInfo.id()))
                .map(gameAnalyzer::analyze)
                .map(this::toGameEvent)
                .ifPresentOrElse(eventPublisher::publishEvent, () ->
                        log.warn("The ended game by id {} hasn't been found", gameInfo.id()));
    }

    @Override
    @Async
    public void notifyAcceptAnswer(AnswerResult answerResult) {
        eventPublisher.publishEvent(new QuestionEvent(this, answerResult));
    }

    public GameEvent toGameEvent(QuizGameAnalysis analysis) {
        return new GameEvent(this, analysis);
    }

}

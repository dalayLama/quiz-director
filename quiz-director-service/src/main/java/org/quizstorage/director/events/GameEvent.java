package org.quizstorage.director.events;

import lombok.Getter;
import org.quizstoradge.director.dto.GameEventType;
import org.quizstoradge.director.dto.GameInfo;
import org.quizstoradge.director.dto.QuizGameAnalysis;
import org.springframework.context.ApplicationEvent;

@Getter
public class GameEvent extends ApplicationEvent {

    private final GameInfo gameInfo;

    private final GameEventType eventType;

    private final QuizGameAnalysis quizGameAnalysis;

    public GameEvent(Object source, GameInfo gameInfo, GameEventType eventType) {
        super(source);
        this.gameInfo = gameInfo;
        this.eventType = eventType;
        this.quizGameAnalysis = null;
    }

    public GameEvent(Object source, QuizGameAnalysis quizGameAnalysis) {
        super(source);
        this.gameInfo = quizGameAnalysis.gameInfo();
        this.eventType = GameEventType.FINISHED_GAME;
        this.quizGameAnalysis = quizGameAnalysis;
    }

}

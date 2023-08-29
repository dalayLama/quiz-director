package org.quizstorage.director.events;

import org.quizstoradge.director.dto.AnswerResult;
import org.quizstoradge.director.dto.GameInfo;

public interface GameNotifier {

    void notifyStartGame(GameInfo gameInfo);

    void notifyEndGame(GameInfo gameInfo);

    void notifyAcceptAnswer(AnswerResult answerResult);
}

package org.quizstorage.director.components;

import org.quizstoradge.director.dto.QuizGameAnalysis;
import org.quizstoradge.director.dto.GameResult;
import org.quizstorage.director.dao.entities.GameQuestion;
import org.quizstorage.director.dao.entities.QuizGame;

public interface GameAnalyzer {

    QuizGameAnalysis analyze(QuizGame game);

    GameResult calculateResult(QuizGame game);

    boolean hasCorrectAnswer(GameQuestion gameQuestion);
}

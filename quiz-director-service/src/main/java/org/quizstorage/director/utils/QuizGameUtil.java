package org.quizstorage.director.utils;

import lombok.experimental.UtilityClass;
import org.quizstorage.director.dao.entities.QuizGame;

@UtilityClass
public class QuizGameUtil {

    public static boolean hasUnansweredQuestions(QuizGame game) {
        return game.getQuestions().stream().anyMatch(q -> !q.isAnswered());
    }

}

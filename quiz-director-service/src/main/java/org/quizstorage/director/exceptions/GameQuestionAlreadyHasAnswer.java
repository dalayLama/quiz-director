package org.quizstorage.director.exceptions;

import org.bson.types.ObjectId;

public class GameQuestionAlreadyHasAnswer extends BadRequest {

    public static final String MESSAGE_TEMPLATE = "Question number %d of the game %s is already answered";

    public GameQuestionAlreadyHasAnswer(ObjectId gameId, int questionNumber) {
        super(MESSAGE_TEMPLATE.formatted(questionNumber, gameId));
    }

}

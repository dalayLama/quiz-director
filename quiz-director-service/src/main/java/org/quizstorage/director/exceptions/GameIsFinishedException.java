package org.quizstorage.director.exceptions;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;

public class GameIsFinishedException extends QuizDirectorServiceException {

    private static final String MESSAGE_TEMPLATE = "Game %s is already finished";

    public GameIsFinishedException(ObjectId gameId) {
        super(MESSAGE_TEMPLATE.formatted(gameId.toString()), HttpStatus.UNPROCESSABLE_ENTITY);
    }

}

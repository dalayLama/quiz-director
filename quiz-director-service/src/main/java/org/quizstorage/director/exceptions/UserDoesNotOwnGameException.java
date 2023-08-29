package org.quizstorage.director.exceptions;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;

public class UserDoesNotOwnGameException extends QuizDirectorServiceException {

    private static final String MESSAGE_TEMPLATE = "Doesn't have rights for the game \"%s\"";

    public UserDoesNotOwnGameException(ObjectId gameId) {
        super(MESSAGE_TEMPLATE.formatted(gameId.toString()), HttpStatus.FORBIDDEN);
    }

}

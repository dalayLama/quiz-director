package org.quizstorage.director.exceptions;

import org.springframework.http.HttpStatus;

public class UserHasUnfinishedGame extends QuizDirectorServiceException {

    private final static String MESSAGE = "Current user has unfinished game";

    public UserHasUnfinishedGame() {
        super(MESSAGE, HttpStatus.UNPROCESSABLE_ENTITY);
    }

}

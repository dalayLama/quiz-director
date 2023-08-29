package org.quizstorage.director.exceptions;

import org.springframework.http.HttpStatus;

public class BadRequest extends QuizDirectorServiceException {

    public BadRequest() {
        super(HttpStatus.BAD_REQUEST);
    }

    public BadRequest(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public BadRequest(String message, Throwable cause) {
        super(message, cause, HttpStatus.BAD_REQUEST);
    }

    public BadRequest(Throwable cause) {
        super(cause, HttpStatus.BAD_REQUEST);
    }

    public BadRequest(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace, HttpStatus.BAD_REQUEST);
    }
}

package org.quizstorage.director.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends QuizDirectorServiceException {
    public NotFoundException() {
        super(HttpStatus.NOT_FOUND);
    }

    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause, HttpStatus.NOT_FOUND);
    }

    public NotFoundException(Throwable cause) {
        super(cause, HttpStatus.NOT_FOUND);
    }

    public NotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace, HttpStatus.NOT_FOUND);
    }
}

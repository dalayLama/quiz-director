package org.quizstorage.director.exceptions;

import org.springframework.http.HttpStatusCode;

public class GameResultGenerationException extends QuizDirectorServiceException {

    public GameResultGenerationException(HttpStatusCode statusCode) {
        super(statusCode);
    }

    public GameResultGenerationException(String message, HttpStatusCode statusCode) {
        super(message, statusCode);
    }

    public GameResultGenerationException(String message, Throwable cause, HttpStatusCode statusCode) {
        super(message, cause, statusCode);
    }

    public GameResultGenerationException(Throwable cause, HttpStatusCode statusCode) {
        super(cause, statusCode);
    }

}

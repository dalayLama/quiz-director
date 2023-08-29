package org.quizstorage.director.exceptions;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

public class QuizDirectorServiceException extends RuntimeException implements ErrorResponse {

    private final HttpStatusCode statusCode;

    public QuizDirectorServiceException(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public QuizDirectorServiceException(String message, HttpStatusCode statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public QuizDirectorServiceException(String message, Throwable cause, HttpStatusCode statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public QuizDirectorServiceException(Throwable cause, HttpStatusCode statusCode) {
        super(cause);
        this.statusCode = statusCode;
    }

    public QuizDirectorServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, HttpStatusCode statusCode) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.statusCode = statusCode;
    }

    @Override
    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    @Override
    public ProblemDetail getBody() {
        return ProblemDetail.forStatus(statusCode);
    }

}

package org.quizstorage.director.exceptions;

public class CurrentGameQuestionNotFound extends NotFoundException {

    private static final String MESSAGE = "Current question for the authenticated user hasn't been found";

    public CurrentGameQuestionNotFound() {
        this(MESSAGE);
    }

    public CurrentGameQuestionNotFound(String message) {
        super(message);
    }

}

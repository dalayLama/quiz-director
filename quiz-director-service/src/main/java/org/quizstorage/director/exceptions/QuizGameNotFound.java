package org.quizstorage.director.exceptions;

import org.bson.types.ObjectId;

public class QuizGameNotFound extends NotFoundException {

    private static final String NOT_FOUND_BY_ID_TEMPLATE = "Quiz Game hasn't been found by id \"%s\"";

    public QuizGameNotFound(ObjectId objectId) {
        super(NOT_FOUND_BY_ID_TEMPLATE.formatted(objectId.toString()));
    }

}

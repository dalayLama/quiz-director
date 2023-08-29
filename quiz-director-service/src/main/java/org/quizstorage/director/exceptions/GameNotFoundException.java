package org.quizstorage.director.exceptions;

import org.bson.types.ObjectId;

public class GameNotFoundException extends NotFoundException {

    private static final String NOT_FOUND_BY_ID_TEMPLATE = "Game hasn't been found by id \"%s\"";

    public GameNotFoundException(ObjectId gameId) {
        super(NOT_FOUND_BY_ID_TEMPLATE.formatted(gameId.toString()));
    }

}

package org.quizstorage.director.components;

import org.quizstorage.director.security.QuizUser;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public interface UserService {

    Optional<QuizUser> getAuthorizedUserData();

    <T> T doAndReturnAsCurrentUser(Function<QuizUser, T> function);

    void doAsCurrentUser(Consumer<QuizUser> consumer);

}

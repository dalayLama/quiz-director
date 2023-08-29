package org.quizstorage.director.dao.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import org.quizstorage.generator.dto.Difficulty;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@ToString
public class UnansweredGameQuestion extends GameQuestion {

    @Field("gameId")
    private ObjectId gameId;

    @Field("userId")
    private String userId;

    @Field("sourceId")
    private String sourceId;

    public UnansweredGameQuestion() {
    }

    public UnansweredGameQuestion(int number,
                                  String question,
                                  Set<String> answers,
                                  Set<String> correctAnswers,
                                  boolean multiplyAnswers,
                                  String category,
                                  Difficulty difficulty,
                                  Set<String> userAnswers,
                                  Instant answerDateTime,
                                  ObjectId gameId,
                                  String userId) {
        super(number, question, answers, correctAnswers, multiplyAnswers, category, difficulty, userAnswers, answerDateTime);
        this.gameId = gameId;
        this.userId = userId;
    }
}

package org.quizstorage.director.dao.entities;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;

@Document("games")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@ToString
public class QuizGame {

    @Id
    private ObjectId id;

    @Field("userId")
    private String userId;

    @Field("sourceId")
    private String sourceId;

    @Field("questions")
    private List<GameQuestion> questions;

    @Field("endDateTime")
    private Instant endDateTime;

    @Transient
    public GameQuestion getQuestionByNumber(int questionNumber) {
        int index = questionNumber - 1;
        if (questions == null || questions.isEmpty() || index < 0 || questionNumber > questions.size()) {
            throw new IllegalArgumentException("Question number is out of numbers of questions");
        }
        return questions.get(index);
    }

    @Transient
    public boolean isFinished() {
        return endDateTime != null;
    }

}

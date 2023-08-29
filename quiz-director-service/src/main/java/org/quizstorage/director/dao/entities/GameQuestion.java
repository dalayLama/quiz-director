package org.quizstorage.director.dao.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;
import org.quizstorage.generator.dto.Difficulty;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@ToString
public class GameQuestion {

    @Field("number")
    private int number;

    @Field("question")
    private String question;

    @Field("answers")
    private Set<String> answers;

    @Field("correctAnswers")
    private Set<String> correctAnswers;

    @Field("multiplyAnswers")
    private boolean multiplyAnswers;

    @Field("category")
    private String category;

    @Field("difficulty")
    private Difficulty difficulty;

    @Field("userAnswers")
    private Set<String> userAnswers;

    @Field("answerDateTime")
    private Instant answerDateTime;

    public boolean isAnswered() {
        return ObjectUtils.isNotEmpty(userAnswers);
    }

}

package org.quizstorage.director.events;

import lombok.Getter;
import org.quizstoradge.director.dto.AnswerResult;
import org.springframework.context.ApplicationEvent;

@Getter
public class QuestionEvent extends ApplicationEvent {

    private final AnswerResult answerResult;

    public QuestionEvent(Object source, AnswerResult answerResult) {
        super(source);
        this.answerResult = answerResult;
    }

}

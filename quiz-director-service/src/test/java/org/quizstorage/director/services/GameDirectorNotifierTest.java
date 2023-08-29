package org.quizstorage.director.services;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quizstoradge.director.dto.AnswerResult;
import org.quizstoradge.director.dto.GameInfo;
import org.quizstoradge.director.dto.GameQuestionDto;
import org.quizstorage.director.events.GameNotifier;
import org.quizstorage.director.utils.TestData;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;

@ExtendWith(MockitoExtension.class)
class GameDirectorNotifierTest {


    @InjectMocks
    private GameDirectorNotifier gameDirectorNotifier;

    @Mock
    private GameDirector delegate;

    @Mock
    private GameNotifier notifier;

    @Test
    public void newGame_shouldNotifyStartGame() {
        // given
        given(delegate.newGame(TestData.QUESTION_SET)).willReturn(TestData.GAME_INFO);

        // when
        GameInfo result = gameDirectorNotifier.newGame(TestData.QUESTION_SET);

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(TestData.GAME_INFO);
        then(notifier).should(only()).notifyStartGame(TestData.GAME_INFO);
    }

    @Test
    public void acceptAnswers_shouldNotifyAcceptAnswer() {
        // given
        ObjectId gameId = new ObjectId();
        int questionNumber = 1;
        Set<String> answers = Set.of("answer1", "answer2");
        AnswerResult expected = AnswerResult.builder().nextQuestion(GameQuestionDto.builder().build()).build();

        given(delegate.acceptAnswers(gameId, questionNumber, answers))
                .willReturn(expected);

        // when
        AnswerResult result = gameDirectorNotifier.acceptAnswers(gameId, questionNumber, answers);

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
        then(notifier).should(only()).notifyAcceptAnswer(expected);
        then(notifier).should(never()).notifyEndGame(any());
    }

    @Test
    public void acceptAnswers_shouldNotifyEndGame() {
        // given
        ObjectId gameId = new ObjectId();
        int questionNumber = 1;
        Set<String> answers = Set.of("answer1", "answer2");
        AnswerResult expected = AnswerResult.builder().build();

        given(delegate.acceptAnswers(gameId, questionNumber, answers))
                .willReturn(expected);

        // when
        AnswerResult result = gameDirectorNotifier.acceptAnswers(gameId, questionNumber, answers);

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
        then(notifier).should().notifyAcceptAnswer(expected);
        then(notifier).should().notifyEndGame(any());
    }

}
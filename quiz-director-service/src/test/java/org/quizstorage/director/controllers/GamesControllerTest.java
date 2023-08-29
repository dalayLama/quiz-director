package org.quizstorage.director.controllers;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.quizstoradge.director.dto.GameInfo;
import org.quizstoradge.director.dto.GameResult;
import org.quizstorage.director.configurations.paths.ApiPaths;
import org.quizstorage.director.services.GameDirector;
import org.quizstorage.director.utils.TestData;
import org.quizstorage.director.utils.TestFilesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GamesController.class)
@Import(ControllersConfiguration.class)
class GamesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameDirector gameDirector;

    @Test
    @WithMockUser
    void shouldInitNewGame() throws Exception {
        String request = TestFilesUtil.readFile("requests/question-set.json");
        String response = TestFilesUtil.readFile("responses/initialized-game.json");


        LocalDateTime localDateTime = LocalDateTime.of(2023, 7, 24, 14, 18, 0);
        ZoneOffset offset = ZoneId.systemDefault().getRules().getOffset(localDateTime);
        Instant start = localDateTime.toInstant(offset);

        GameInfo game = GameInfo.builder().id("123").userId("555").sourceId("sourceId").start(start).build();

        given(gameDirector.newGame(TestData.QUESTION_SET)).willReturn(game);

        mockMvc.perform(post(ApiPaths.GAMES_V1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(content().json(response))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void shouldReturn400WhenQuestionSetIsNotValid() throws Exception {
        String request = TestFilesUtil.readFile("requests/not-valid-question-set.json");

        mockMvc.perform(post(ApiPaths.GAMES_V1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andDo(print());

        then(gameDirector).should(never()).newGame(any());
    }

    @Test
    @WithMockUser
    void shouldReturnCurrentQuestion() throws Exception {
        String response = TestFilesUtil.readFile("responses/game-question.json");

        given(gameDirector.getCurrentQuestion()).willReturn(Optional.of(TestData.GAME_QUESTION_DTO));

        mockMvc.perform(get(ApiPaths.CURRENT_QUESTION_V1))
                .andExpect(status().isOk())
                .andExpect(content().json(response))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void shouldReturn204WhenCurrentQuestionsIsAbsent() throws Exception {
        given(gameDirector.getCurrentQuestion()).willReturn(Optional.empty());

        mockMvc.perform(get(ApiPaths.CURRENT_QUESTION_V1))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @WithMockUser
    void shouldReturnAnswerResult() throws Exception {
        String response = TestFilesUtil.readFile("responses/answer-result.json");
        String request = TestFilesUtil.readFile("requests/answers.json");
        ObjectId gameId = new ObjectId();
        int questionNumber = 1;
        Set<String> answers = Set.of("answer");

        given(gameDirector.acceptAnswers(gameId, questionNumber, answers)).willReturn(TestData.ANSWER_RESULT);

        mockMvc.perform(post(ApiPaths.ACCEPT_ANSWER_V1, gameId.toString(), questionNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(content().json(response))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void shouldReturnBadRequestWhenAnswerIsBlank() throws Exception {
        ObjectId gameId = new ObjectId();
        int questionNumber = 1;

        mockMvc.perform(post(ApiPaths.ACCEPT_ANSWER_V1, gameId.toString(), questionNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest())
                .andDo(print());

        then(gameDirector).should(never()).acceptAnswers(any(), anyInt(), any());
    }

    @Test
    @WithMockUser
    void shouldReturnFinishedGames() throws Exception {
        String response = TestFilesUtil.readFile("responses/finished-games.json");

        given(gameDirector.findFinishedGames()).willReturn(List.of(TestData.GAME_INFO));

        mockMvc.perform(get(ApiPaths.FINISHED_GAMES_V1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(response))
                .andDo(print());

    }

    @Test
    @WithMockUser
    void shouldReturnGameResult() throws Exception {
        GameResult gameResult = TestData.GAME_RESULT;
        ObjectId gameId = new ObjectId();
        String response = TestFilesUtil.readFile("responses/game-result.json");

        given(gameDirector.getGameResult(gameId)).willReturn(Optional.of(gameResult));

        mockMvc.perform(get(ApiPaths.GAME_RESULT_V1, gameId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(response))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void shouldReturn404WhenGameIsAbsent() throws Exception {
        ObjectId gameId = new ObjectId();

        given(gameDirector.getGameResult(gameId)).willReturn(Optional.empty());

        mockMvc.perform(get(ApiPaths.GAME_RESULT_V1, gameId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

}
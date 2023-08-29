package org.quizstorage.director.controllers;

import org.junit.jupiter.api.Test;
import org.quizstorage.director.components.UserService;
import org.quizstorage.director.configurations.paths.ApiPaths;
import org.quizstorage.director.dao.entities.UserWebSocketTokenData;
import org.quizstorage.director.security.QuizUser;
import org.quizstorage.director.services.UserWebSocketTokenDataService;
import org.quizstorage.director.utils.TestData;
import org.quizstorage.director.utils.TestFilesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WebSocketTokenController.class)
public class WebSocketTokenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserWebSocketTokenDataService tokenDataService;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser
    public void shouldGenerateToken() throws Exception {
        UserWebSocketTokenData userWebSocketTokenData = TestData.USER_WEB_SOCKET_TOKEN_DATA;
        String tokenId = TestFilesUtil.readFile("responses/websocket-token.json");

        willAnswer(invocation -> {
            Function<QuizUser, UserWebSocketTokenData> function = invocation.getArgument(0);
            return function.apply(TestData.QUIZ_USER);
        }).given(userService).doAndReturnAsCurrentUser(any(Function.class));
        given(tokenDataService.generateToken(TestData.QUIZ_USER)).willReturn(userWebSocketTokenData);

        mockMvc.perform(get(ApiPaths.WEB_SOCKET_TOKEN_V1))
                .andExpect(status().isOk())
                .andExpect(content().json(tokenId))
                .andDo(print());
    }

}
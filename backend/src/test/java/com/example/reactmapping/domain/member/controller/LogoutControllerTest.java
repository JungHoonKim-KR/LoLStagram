//package com.example.reactmapping.domain.member.controller;
//
//import com.example.reactmapping.domain.member.service.LogoutService;
//import com.example.reactmapping.global.norm.Token;
//import jakarta.servlet.http.Cookie;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
//import static org.mockito.Mockito.verify;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(controllers = LogoutController.class)
//class LogoutControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private LogoutService logoutService;
//
//    @Test
//    void logoutTest() throws Exception {
//        //Given
//        String accessToken = "mocked-access-token";
//        String refreshToken = "mocked-refresh-token";
//
//        //when&then
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/logout")
//                .contentType(MediaType.APPLICATION_JSON)
//                .header("Authorization", "Bearer " + accessToken)
//                .cookie(new Cookie(Token.TokenName.refreshToken, refreshToken)))
//                .andExpect(status().isOk());
//
//        verify(logoutService).logout(accessToken,refreshToken);
//
//    }
//
//
//}
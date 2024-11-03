//package com.example.reactmapping.domain.member.controller;
//
//import com.example.reactmapping.domain.member.dto.LoginInfo;
//import com.example.reactmapping.domain.member.dto.LoginRequestDto;
//import com.example.reactmapping.domain.member.service.LoginService;
//import com.example.reactmapping.global.norm.Token;
//import com.example.reactmapping.global.security.cookie.CookieUtil;
//import com.example.reactmapping.global.security.jwt.JwtUtil;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.http.Cookie;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import org.springframework.test.web.servlet.MockMvc;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//
//@AutoConfigureMockMvc
//@WebMvcTest(controllers = LoginController.class)
//class MemberControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//    @MockBean
//    private LoginService loginService;
//
//    @MockBean
//    private JwtUtil jwtUtil;
//
//    @MockBean
//    private CookieUtil cookieUtil;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    void loginTest() throws Exception {
//        // Given
//        String email = "test@naver.com";
//        String password = "1234";
//        String accessToken = "mocked-access-token";
//        String refreshToken = "mocked-refresh-token";
//        String username = "testuser";
//
//        LoginInfo loginInfo = new LoginInfo(accessToken, refreshToken, username, null, null); // 필요한 필드만 채우세요.
//
//        // 실제 값으로 모킹
//        when(loginService.login("test@naver.com", "1234")).thenReturn(loginInfo);
//        when(jwtUtil.createToken(email, Token.TokenType.ACCESS.name())).thenReturn(accessToken);
//        when(cookieUtil.createCookie(Token.TokenName.refreshToken, refreshToken))
//                .thenReturn(new Cookie(Token.TokenName.refreshToken, refreshToken));
//
//        LoginRequestDto loginRequestDto = new LoginRequestDto(email, password,"솔랭",null);
//
//        // When & Then
//        mockMvc.perform(MockMvcRequestBuilders.post("/login/normal")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(loginRequestDto)))
//                .andExpect(status().isOk())
//                .andExpect(cookie().value(Token.TokenName.refreshToken, refreshToken))
//                .andExpect(jsonPath("$.accessToken").value(accessToken))
//                .andExpect(jsonPath("$.username").value(username));
//    }
//}
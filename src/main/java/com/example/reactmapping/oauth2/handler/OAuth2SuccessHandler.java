package com.example.reactmapping.oauth2.handler;

import com.example.reactmapping.global.security.jwt.JwtService;
import com.example.reactmapping.global.security.jwt.TokenDto;
import com.example.reactmapping.oauth2.OAuth2.CustomOAuth2User;
import com.example.reactmapping.domain.member.domain.Member;
import com.example.reactmapping.global.norm.Token;
import com.example.reactmapping.domain.member.repository.MemberRepository;
import com.example.reactmapping.global.security.cookie.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final MemberRepository memberRepository;
    private final JwtService jwtService;
    private final CookieUtil cookieUtil;

    //loaduser()를 통해 Authentication에 유저 정보가 들어감
    //여기서 유저 정보를 이용해 토큰을 만들어서 클라이언트에게 전달해야함
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        log.info("소셜 로그인 성공");
        CustomOAuth2User customOAuth2User= (CustomOAuth2User) authentication.getPrincipal();
        String emailId = customOAuth2User.getEmail();
        String username = customOAuth2User.getName();

        log.info("유저 이메일: "+ emailId);
        log.info("유저 이름: "+ username);

        // 신규 화원인 경우
        Optional<Member> memberByEmail = memberRepository.findMemberByEmailId(emailId);
        if(!memberByEmail.isPresent()){
            join(response, emailId, username);
            return;
        }
        Member member = memberByEmail.get();
        sendToken(response, member);
//        sendAuthenticationCode(request, response, emailId);

//        HTTP 응답 헤더에 토큰을 포함시키는 것은 가능하지만, 이 방법을 사용하면 클라이언트가 토큰을 받아올 수 없는 상황이 발생할 수 있습니다.
//                웹 브라우저 환경에서는 JavaScript가 아닌 사용자의 브라우저를 통해 리다이렉트를 수행하는 경우, 브라우저는 리다이렉트 대상 페이지로 이동하면서 응답 헤더를 무시합니다.
//                즉, 응답 헤더에 있는 토큰 정보를 JavaScript에서 읽어올 수 없게 됩니다.
//                이 문제를 해결하기 위해 토큰을 URL의 쿼리 파라미터로 전달하는 방법을 많이 사용합니다. 하지만 이 방법은 토큰이 노출될 위험이 있으므로, HTTPS를 통해 통신하는 것이 필요합니다.
//        보안을 더 강화하고 싶다면, 토큰 대신에 일회용 코드를 발급하고 이를 클라이언트에 전달하는 방법을 사용할 수 있습니다. 클라이언트는 이 코드를 사용하여 서버에 다시 요청을 보내고,
//        서버는 이 때 토큰을 응답으로 전달하는 방식입니다. 이 방법은 OAuth2의 Authorization Code Flow에서 사용하는 방법입니다.


    }

    private void sendToken(HttpServletResponse response, Member member) throws IOException {
        TokenDto tokenDto = jwtService.generateToken(member.getEmailId());
        response.addCookie(cookieUtil.createCookie(Token.TokenName.accessToken,tokenDto.getAccessToken()));
        response.addCookie(cookieUtil.createCookie(Token.TokenName.refreshToken,tokenDto.getRefreshToken()));
        response.sendRedirect("http://localhost:8080/#/oauth/callback");
    }
    //    private void sendAuthenticationCode(HttpServletRequest request, HttpServletResponse response, String emailId) throws IOException {
//        HttpSession session = request.getSession();
//        String authenticationCode = UUID.randomUUID().toString();
//        session.setAttribute("AuthenticationDto", new AuthenticationDto(emailId,authenticationCode));
//        response.addCookie(createCookie("authenticationCode",authenticationCode));
//        response.sendRedirect("http://localhost:8080/#/oauth/callback?authenticationCode=" + authenticationCode);
//    }

    private void join(HttpServletResponse response, String emailId, String username) throws IOException {
        String encodedEmail = URLEncoder.encode(emailId, StandardCharsets.UTF_8);
        String encodedUsername = URLEncoder.encode(username, StandardCharsets.UTF_8);
        response.sendRedirect("http://localhost:8080/#/join?emailId="+encodedEmail + "&username="+encodedUsername);
    }
}

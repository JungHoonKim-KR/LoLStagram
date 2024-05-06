package com.example.reactmapping.config;

import com.example.reactmapping.config.jwt.JwtFilter;
import com.example.reactmapping.config.jwt.JwtUtil;
import com.example.reactmapping.handler.OAuth2LoginFailureHandler;
import com.example.reactmapping.handler.OAuth2SuccessHandler;
import com.example.reactmapping.repository.RefreshTokenRepository;
import com.example.reactmapping.service.CustomOauth2UserService;
import com.example.reactmapping.service.LogoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final LogoutService logoutService;
    private final CustomOauth2UserService customOauth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return
                httpSecurity
                        .cors(cors -> cors.configurationSource(request -> {
                            var corsConfiguration = new CorsConfiguration();
                                corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000"));
                                corsConfiguration.setAllowedHeaders(List.of("*"));
                                corsConfiguration.setAllowCredentials(true);
                            return corsConfiguration;
                        }))
                        .csrf((auth) -> auth.disable())
                        .httpBasic((auth)->auth.disable())

                        .headers((headers) -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                        .sessionManagement((sessionManagement)->sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        //접근 권한 관리
                        .authorizeHttpRequests(auth -> auth
                                //react 라우터들에 대한 접근 권한 허용
                                .requestMatchers(HttpMethod.GET, "/**").permitAll()
                                .requestMatchers("","/","/auth/**","/oauthLogin","/swagger-ui/**","/api-docs/**","/question","/test","/static/**").permitAll()
                                .requestMatchers("/admin").hasRole("ADMIN")
                                .anyRequest().authenticated()
                        )
                        //권한 불일치 -> login page로 이동
                        .formLogin(auth -> auth.disable())
                        .addFilterBefore(new JwtFilter(jwtUtil,refreshTokenRepository), UsernamePasswordAuthenticationFilter.class)
                        .logout(logoutConfig -> { logoutConfig
                                .logoutUrl("/logout")
                                .addLogoutHandler(logoutService)
                                .logoutSuccessHandler((request, response, authentication) ->
                                {SecurityContextHolder.clearContext();
                                    response.setContentType("application/json");
                                    response.setCharacterEncoding("UTF-8");
                                    response.getWriter().write("{\"message\":\"로그아웃 합니다.\"}");
                                });
                        })
                        .oauth2Login(oauth -> oauth
                                .successHandler(oAuth2SuccessHandler)
                                .failureHandler(oAuth2LoginFailureHandler)
                                .userInfoEndpoint((userInfoEndpointConfig -> userInfoEndpointConfig.userService(customOauth2UserService)))
                        )
                        .build();

    }


}

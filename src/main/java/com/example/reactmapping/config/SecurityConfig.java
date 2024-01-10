package com.example.reactmapping.config;

import com.example.reactmapping.config.jwt.JwtFilter;
import com.example.reactmapping.config.jwt.JwtUtil;
import com.example.reactmapping.handler.OAuth2LoginFailureHandler;
import com.example.reactmapping.handler.OAuth2SuccessHandler;
import com.example.reactmapping.repository.BlackListRepository;
import com.example.reactmapping.repository.RefreshTokenRepository;
import com.example.reactmapping.service.LogoutService;
import com.example.reactmapping.service.OAuth2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final LogoutService logoutService;
    private final OAuth2Service oAuth2Service;
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
                            if (request.getRequestURI().startsWith("/write")) {
                                corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000"));
                                corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                                corsConfiguration.setAllowedHeaders(List.of("*"));
                            }
                            return corsConfiguration;
                        }))
                        .csrf((auth) -> auth.disable())
                        .httpBasic((auth)->auth.disable())

                        .headers((headers) -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                        .sessionManagement((sessionManagement)->sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        //접근 권한 관리
                        .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/","/login","/swagger-ui/**","/join","/api-docs/**").permitAll()
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
                                .userInfoEndpoint(EndPoint -> EndPoint.userService(oAuth2Service))
                        )

                        .build();

    }


}

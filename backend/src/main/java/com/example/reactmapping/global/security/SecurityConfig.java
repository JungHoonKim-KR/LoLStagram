package com.example.reactmapping.global.security;

import com.example.reactmapping.global.norm.URL;
import com.example.reactmapping.global.security.jwt.JwtFilter;
import com.example.reactmapping.global.security.jwt.JwtService;
import com.example.reactmapping.global.security.jwt.JwtUtil;
import com.example.reactmapping.oauth2.handler.OAuth2LoginFailureHandler;
import com.example.reactmapping.oauth2.handler.OAuth2SuccessHandler;
import com.example.reactmapping.oauth2.CustomOauth2UserService;
import com.example.reactmapping.global.security.cookie.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final URL url;
    private final JwtUtil jwtUtil;
    private final CustomOauth2UserService customOauth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final CookieUtil cookieUtil;
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean // Spring Security 무시시키기
    public WebSecurityCustomizer webSecurityCustomizer() {
        return new WebSecurityCustomizer() {
            @Override
            public void customize(WebSecurity web) {
                // /error -> spring에서 기본제공하는 것
                web.ignoring()
                        .requestMatchers("/swagger-ui/**","/api-docs/**","/static/**");
            }
        };
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, JwtService jwtService) throws Exception {
        return
                httpSecurity
                        //cors
                        .cors(cors -> cors.configurationSource(request -> {
                            var corsConfiguration = new CorsConfiguration();
                            corsConfiguration.setAllowedOrigins(List.of(url.getClient()));
                            corsConfiguration.setAllowedMethods(List.of("GET","POST","PUT","DELETE"));
                            corsConfiguration.setAllowCredentials(true);
                            return corsConfiguration;
                        }))
                        .csrf(AbstractHttpConfigurer::disable)
                        .httpBasic(AbstractHttpConfigurer::disable)

                        .headers((headers) -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                        .sessionManagement((sessionManagement)->sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        //접근 권한 관리
                        .authorizeHttpRequests(auth -> auth
                                        //react 라우터들에 대한 접근 권한 허용
                                        .requestMatchers(HttpMethod.GET, "/**").permitAll()
                                        .requestMatchers("","/","/login/**","/join/**","/actuator/**","/summoner/enrollalldata").permitAll()
//                                .requestMatchers("/admin").hasRole("ADMIN")
                                        .anyRequest().authenticated()
                        )
                        //권한 불일치 -> login page로 이동
                        .formLogin(auth -> auth.disable())
                        .addFilterBefore(new JwtFilter(jwtUtil,jwtService,cookieUtil), UsernamePasswordAuthenticationFilter.class)
                        .logout(logout -> logout.disable()
                                )
                        .oauth2Login(oauth -> oauth
                                .loginPage("/login/normal")
                                .permitAll()
                                .successHandler(oAuth2SuccessHandler)
                                .failureHandler(oAuth2LoginFailureHandler)
                                .userInfoEndpoint((userInfoEndpointConfig -> userInfoEndpointConfig.userService(customOauth2UserService)))
                        )
                        .build();

    }

}

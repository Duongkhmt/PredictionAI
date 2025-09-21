package com.example.DuDoanAI.config;

import com.example.DuDoanAI.service.CustomOAuth2UserService;
import com.example.DuDoanAI.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public  class SecurityConfig {

        @Autowired
        private CustomUserDetailsService userDetailsService;

        @Autowired
        private CustomOAuth2UserService customOAuth2UserService;

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public DaoAuthenticationProvider authenticationProvider() {
            DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
            authProvider.setUserDetailsService(userDetailsService);
            authProvider.setPasswordEncoder(passwordEncoder());
            return authProvider;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .authorizeHttpRequests(authz -> authz
                            .requestMatchers(
                                    AntPathRequestMatcher.antMatcher("/"),
                                    AntPathRequestMatcher.antMatcher("/home"),
                                    AntPathRequestMatcher.antMatcher("/register"),
                                    AntPathRequestMatcher.antMatcher("/login"),
                                    AntPathRequestMatcher.antMatcher("/forgot-password"),
                                    AntPathRequestMatcher.antMatcher("/reset-password"),

                                    //thêm nè
                                    AntPathRequestMatcher.antMatcher("/image-analysis"),
                                    AntPathRequestMatcher.antMatcher("/image-analysis/**"),

                                    AntPathRequestMatcher.antMatcher("/api/gemini"),



                                    AntPathRequestMatcher.antMatcher("/css/**"),
                                    AntPathRequestMatcher.antMatcher("/js/**"),
                                    AntPathRequestMatcher.antMatcher("/images/**"),
                                    AntPathRequestMatcher.antMatcher("/webjars/**"),
                                    AntPathRequestMatcher.antMatcher("/api/test/**")
                            ).permitAll()
                            .requestMatchers(
                                    "/dashboard", "/analyze", "/analyze-sentiment",
                                    "/predictions", "/statistics", "/search",
                                    "/update-advice/**", "/delete-analysis/**", "/delete-all",

                                    "/image-analysis/history",
                                    "/image-analysis/detail/**",
                                    "/image-analysis/delete/**",

                                    "/chatbot",
                                    "/chatbot/**"

                            ).authenticated()
                            .requestMatchers("/admin/**").hasRole("ADMIN")
                            .anyRequest().authenticated()
                    )
                    // Form login (username/password)
                    .formLogin(form -> form
                            .loginPage("/login")
                            .defaultSuccessUrl("/dashboard", true)
                            .failureUrl("/login?error=true")
                            .permitAll()
                    )
                    // Google OAuth2 login
                    .oauth2Login(oauth2 -> oauth2
                            .loginPage("/login")
                            .userInfoEndpoint(userInfo -> userInfo
                                    .userService(customOAuth2UserService)
                            )
                            .defaultSuccessUrl("/dashboard", true)
                    )
                    // Remember-me
                    .rememberMe(remember -> remember
                            .key("uniqueAndSecretKey")
                            .tokenValiditySeconds(86400) // 24h
                            .userDetailsService(userDetailsService)
                    )
                    // Logout
                    .logout(logout -> logout
                            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                            .logoutSuccessUrl("/?logout=true")
                            .deleteCookies("JSESSIONID", "remember-me")
                            .invalidateHttpSession(true)
                            .permitAll()
                    )

                    // THÊM DÒNG NÀY để tắt CSRF cho các endpoint API
                    .csrf(csrf -> csrf
                            .ignoringRequestMatchers(
                                    "/chatbot/chat",
                                    "/chatbot/chat-with-context"
                            )
                    )

                    // Xử lý lỗi quyền truy cập
                    .exceptionHandling(ex -> ex
                            .accessDeniedPage("/access-denied")
                    );

            return http.build();
        }

}




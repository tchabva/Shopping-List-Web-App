package uk.project.shoppinglistwebapp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(authRequest ->
                        authRequest
                                .requestMatchers( "/h2-console/**").permitAll()
                                .anyRequest().authenticated())
                .headers(headers ->
                        headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                ).oauth2Login(Customizer.withDefaults());
        httpSecurity.csrf(csrf ->
                csrf.ignoringRequestMatchers(
                        new AntPathRequestMatcher("/h2-console/**")
                ).csrfTokenRepository(
                        CookieCsrfTokenRepository.withHttpOnlyFalse()
                ));
        return httpSecurity.build();
    }
}

package it.valeriovaudi.ui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@SpringBootApplication
public class UiApplication {

    public static void main(String[] args) {
        SpringApplication.run(UiApplication.class, args);
    }

}

@Configuration(proxyBeanMethods = false)
class SecurityConfig {


    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.headers(configurer -> configurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        http.logout(logoutConfigurer ->
                logoutConfigurer.deleteCookies("opbs")
                        .invalidateHttpSession(true)
                        .logoutSuccessUrl("/index")
        );


        http.oauth2Login(loginConfigurer -> loginConfigurer.defaultSuccessUrl("/index"));

        http.authorizeHttpRequests(
                auth ->
                        auth
                                .requestMatchers("/index.html").hasRole("USER")
                                .requestMatchers("/messages.html").hasRole("USER")
                                .anyRequest().permitAll()
        );

        return http.build();
    }

}
package it.valeriovaudi.ui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static java.util.Arrays.asList;

@SpringBootApplication
public class UiApplication {

    public static void main(String[] args) {
        SpringApplication.run(UiApplication.class, args);
    }

}

@Configuration(proxyBeanMethods = false)
class SecurityConfig {


    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http.csrf().disable().authorizeExchange()
                .pathMatchers("/index.html").hasRole("USER")
                .pathMatchers("/messages.html").hasRole("ADMIN")
                .anyExchange().permitAll()
                .and().formLogin()
                .and().logout()
                .and().build();
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .passwordEncoder(pswd -> pswd)
                .username("user")
                .password("secret")
                .roles("USER")
                .build();

        UserDetails admin = User.builder()
                .passwordEncoder(pswd -> pswd)
                .username("admin")
                .password("secret")
                .roles("ADMIN")
                .build();

        return new MapReactiveUserDetailsService(asList(admin, user));
    }
}
package it.valeriovaudi.ui;

import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static java.util.Arrays.asList;

@EnableDiscoveryClient
@SpringBootApplication
public class UiApplication {

    public static void main(String[] args) {
        SpringApplication.run(UiApplication.class, args);
    }

}


@Data
@ToString
@ConfigurationProperties(prefix = "login")
class LoginPageConfig {

    private String page = "/login";

}

@EnableConfigurationProperties(LoginPageConfig.class)
@EnableWebFluxSecurity
class SecurityConfig {

    @Autowired
    private LoginPageConfig loginPageConfig;

    @Bean
    @RefreshScope
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        System.out.println("refresh!");
        System.out.println(loginPageConfig);
        return http.csrf().disable().authorizeExchange()
                .pathMatchers("/index.html").hasRole("USER")
                .pathMatchers("/messages.html").hasRole("ADMIN")
                .anyExchange().permitAll()
                .and().formLogin().loginPage(loginPageConfig.getPage())
                .and().logout()
                .and().build();
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("secret")
                .roles("USER")
                .build();

        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("secret")
                .roles("ADMIN")
                .build();

        return new MapReactiveUserDetailsService(asList(admin, user));
    }
}
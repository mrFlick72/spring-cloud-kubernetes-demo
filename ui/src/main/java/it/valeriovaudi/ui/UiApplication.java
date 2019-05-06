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
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import static java.util.Arrays.asList;
import static org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.pathMatchers;

@EnableDiscoveryClient
@SpringBootApplication
public class UiApplication {

    public static void main(String[] args) {
        SpringApplication.run(UiApplication.class, args);
    }

}

@Controller
@EnableConfigurationProperties(LoginPageConfig.class)
class LoginController {

    private final LoginPageConfig loginPageConfig;

    LoginController(LoginPageConfig loginPageConfig) {
        this.loginPageConfig = loginPageConfig;
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("processingUrl", loginPageConfig.getProcessingUrl());
        return "login";
    }
}

@Data
@ToString
@ConfigurationProperties(prefix = "login")
class LoginPageConfig {

    private static final String DEFAULT_LOGIN_PATH = "/login";

    private String page = DEFAULT_LOGIN_PATH;
    private String requiresAuthenticationMatcher = DEFAULT_LOGIN_PATH;
    private String processingUrl = DEFAULT_LOGIN_PATH;
    private String successAuthenticationPath="/";

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
                .requiresAuthenticationMatcher(pathMatchers(HttpMethod.POST, loginPageConfig.getRequiresAuthenticationMatcher()))
                .authenticationSuccessHandler(new RedirectServerAuthenticationSuccessHandler(loginPageConfig.getSuccessAuthenticationPath()))
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
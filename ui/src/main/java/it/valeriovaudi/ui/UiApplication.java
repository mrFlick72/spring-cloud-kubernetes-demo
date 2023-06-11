package it.valeriovaudi.ui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.SecurityFilterChain;

import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
public class UiApplication {

    public static void main(String[] args) {
        SpringApplication.run(UiApplication.class, args);
    }

}

class UserIntrospectorOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final OidcUserService delegate;

    public UserIntrospectorOidcUserService(OidcUserService delegate) {
        this.delegate = delegate;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = delegate.loadUser(userRequest);
        Collection<GrantedAuthority> mappedAuthorities = authoritiesFor(oidcUser);

        return new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
    }


    private Set<GrantedAuthority> authoritiesFor(OidcUser user) {
        List<String> authorities = authoritiesFrom(user);
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .map(authority -> new OidcUserAuthority(authority.getAuthority(), user.getIdToken(), user.getUserInfo()))
                .collect(Collectors.toSet());
    }

    private List<String> authoritiesFrom(OidcUser oidcUser) {
        List<String> authoritiesClaim = (List<String>) oidcUser.getClaimAsMap("realm_access").get("roles");
        return Optional.ofNullable(authoritiesClaim).orElse(Collections.emptyList());
    }
}

@EnableWebSecurity
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


        http.oauth2Login(configurer ->
                configurer.defaultSuccessUrl("/index").userInfoEndpoint(config ->
                        config.oidcUserService(
                                new UserIntrospectorOidcUserService(new OidcUserService()))
                ));

        http.authorizeHttpRequests(
                auth ->
                        auth
                                .requestMatchers("/index.html").hasAuthority("ADMIN")
                                .requestMatchers("/messages.html").hasAuthority("USER")
                                .anyRequest().permitAll()
        );


        return http.build();
    }

}
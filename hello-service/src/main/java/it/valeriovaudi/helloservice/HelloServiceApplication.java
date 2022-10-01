package it.valeriovaudi.helloservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.UUID;

import static java.lang.String.format;
import static org.springframework.web.reactive.function.BodyInserters.*;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static reactor.core.publisher.Mono.just;

@SpringBootApplication
public class HelloServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloServiceApplication.class, args);
    }


    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }
}

@Slf4j
@Service
class HelloService {

    private static final String DEFAULT_MESSAGE = "no special message for you today :(";
    private static final String TEMPLATE = "Hello %s from service instance %s the special message for you to day is %s";
    private static final String INSTANCE_ID = UUID.randomUUID().toString();

    private final String helloServiceUri;
    private final WebClient webClientBuilder;

    HelloService(@Value("${hello-service-uri:}") String helloServiceUri, WebClient.Builder webClientBuilder) {
        this.helloServiceUri = helloServiceUri;
        this.webClientBuilder = webClientBuilder.build();
    }

    Mono<String> sayHello(String name) {
        return webClientBuilder.get()
                .uri(helloServiceUri)
                .retrieve()
                .bodyToMono(HashMap.class)
                .flatMap(payload -> just(format(TEMPLATE, name, INSTANCE_ID, payload.getOrDefault("message", DEFAULT_MESSAGE))));
    }
}

@Configuration
class RouteConfig {

    private final HelloService helloService;

    RouteConfig(HelloService helloService) {
        this.helloService = helloService;
    }

    @Bean
    public RouterFunction routerFunction() {
        return RouterFunctions.route()
                .GET("/hello/{name}", sayHelloHandler())
                .build();
    }

    private HandlerFunction<ServerResponse> sayHelloHandler() {
        return request -> helloService.sayHello(request.pathVariable("name"))
                .flatMap(helloMessage -> ok().body(fromValue(helloMessage)));
    }
}

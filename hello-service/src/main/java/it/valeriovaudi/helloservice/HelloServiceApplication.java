package it.valeriovaudi.helloservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static java.lang.String.*;
import static org.springframework.web.reactive.function.server.ServerResponse.*;

@EnableDiscoveryClient
@SpringBootApplication
public class HelloServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloServiceApplication.class, args);
    }

    @Bean
    @LoadBalanced
    public WebClient webClient() {
        return WebClient.builder()
                .build();
    }
}

@Service
class HelloService {

    private final String instanceID = UUID.randomUUID().toString();
    private final String template = "Hello %s from service instance %s the special message for you to day is %s";

    private final WebClient webClient;

    HelloService(WebClient webClient) {
        this.webClient = webClient;
    }


    Mono<String> sayHello(String name) {
        return webClient.get()
                .uri("http://message-service/message/random")
                .exchange()
                .flatMap(clientResponse -> clientResponse.bodyToMono(String.class))
                .flatMap(message -> Mono.just(format(template, name, instanceID, message)));
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
                .flatMap(helloMessage -> ok().body(BodyInserters.fromObject(helloMessage)));
    }
}

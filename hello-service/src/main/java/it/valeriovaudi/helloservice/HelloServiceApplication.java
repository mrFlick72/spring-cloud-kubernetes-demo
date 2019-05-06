package it.valeriovaudi.helloservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

import java.util.HashMap;
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
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }
}

@Slf4j
@Service
class HelloService {

    private final String instanceID = UUID.randomUUID().toString();
    private final String template = "Hello %s from service instance %s the special message for you to day is %s";

    private final String helloServiceUri;
    private WebClient.Builder webClientBuilder;

    HelloService(@Value("${hello-service-uri:}") String helloServiceUri, WebClient.Builder webClientBuilder) {
        this.helloServiceUri = helloServiceUri;
        this.webClientBuilder = webClientBuilder;
    }

    Mono<String> sayHello(String name) {
        System.out.println("helloServiceUri: " + helloServiceUri);
        return webClientBuilder.build().get()
                .uri(helloServiceUri)
                .retrieve()
                .bodyToMono(HashMap.class)
                .flatMap(payload -> Mono.just(format(template, name, instanceID, payload.getOrDefault("message", "no special message for you today :("))));
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

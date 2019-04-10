package it.valeriovaudi.helloservice;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.UUID;

import static java.lang.String.*;
import static org.springframework.web.reactive.function.BodyInserters.*;
import static org.springframework.web.reactive.function.server.ServerResponse.*;

@SpringBootApplication
public class HelloServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloServiceApplication.class, args);
    }

}

@Configuration
class RouteConfig {

    private final String instanceID = UUID.randomUUID().toString();

    @Bean
    public RouterFunction routerFunction() {
        return RouterFunctions.route()
                .GET("/hello-service/hello/{name}", sayHelloHandler())
                .build();
    }

    private HandlerFunction<ServerResponse> sayHelloHandler() {
        return request -> ok().body(fromObject(format("Hello %s from service instance %s",
                request.pathVariable("name"), instanceID)));
    }
}

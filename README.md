# Spring Cloud Kubernetes Hello World

Spring Cloud is an umbrella of projects that help us to build Cloud Native Java application using Spring Ecosystem. 
Here I will show how, using the Spring Cloud Abstraction, it is very easy to switch between specific vendor implementation 
without change one line of code. In particular this application can run on Spring Cloud Netflix and the latest Spring Cloud Kubernetes 
only availing on profiled Maven/Gradle build and Spring profile.

## The use case

The sample application is a very simple hello world web application. This application is thought for explore service composition, 
client load balancing and configuration management. The web main application is composed by two pages under security:
  * a web page for standard users in order to get a special hello with a random special message for you 
  * a web page for admin user in order to manage the special message list. 
  * there exist a prebuilt user for both web application
    * username user and password secret for /index.html web application 
    * username admin and password secret for /messages.html web application
 
 ### Web Application for simple user screenshot
 ![](https://raw.githubusercontent.com/mrFlick72/spring-cloud-kubernetes-demo/master/images/user_webapp.png)
 
 
 ### Web Application for admin user screenshot
 ![](https://raw.githubusercontent.com/mrFlick72/spring-cloud-kubernetes-demo/master/images/messages_webapp.png)
 
## The technology stack

In this project you can see used many technologies like:

* Spring Cloud Gateway: the reactive Spring counterpart of Zuul, build on Spring 5.x and Webflux
* Spring Cloud Kubernetes
* Spring Cloud LoadBalancer
* Spring Reactive Data Mongo
* Spring WebFlux
* Spring Boot 2.7.x
* Spring Session
* Spring Reactive Security
* Java/Kotlin


## How it works 

Basically on the UI thankful to Spring Cloud Gateway, similarity to Zuul, we can configure a micro proxy between ui-interface and the backend services.  All the magic 
is performed by the yml configuration. Even if, in any talk on Spring Cloud Gateway probably you can see the java config way, in this example I preferred yml config in order to 
can benefit of hot reload route. The configured routes will be useful to get the hello message on the main web application and create or delete special messages on the 
messages web application accessed by admin users. the snippet of code that implements this magic is like below:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: hello-service
          uri: lb://hello-service/
          predicates:
            - Path=/hello-service/**
          filters:
            - StripPrefix=1

        - id: message-service
          uri: lb://message-service/
          predicates:
            - Path=/message-service/**
          filters:
            - StripPrefix=1
```

On hello-service, instead the integration with message-service is performed by a classic rest service call using a WebClient.Builder annotated with @LoadBalanced in order to 
benefit of the *LoadBalancerExchangeFilterFunction* injected by spring for us. The configuration is very simple and the usage is a classical rest service call via WebClient:

#### load balancer confguration 
```java
   @SpringBootApplication
   public class HelloServiceApplication {
    
        ...

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }
    
   }
```
#### service integration

```java

@Slf4j
@Service
class HelloService {

    ...

    Mono<String> sayHello(String name) {
        return webClientBuilder.get()
                .uri(helloServiceUri)
                .retrieve()
                .bodyToMono(HashMap.class)
                .flatMap(payload -> just(format(TEMPLATE, name, INSTANCE_ID, payload.getOrDefault("message", DEFAULT_MESSAGE))));
    }
}
```

The application.yml configuration provided via config map. The benefit of use configmap 
with Spring Cloud Kubernetes is that configuring restart actuator endpoint with spring cloud kubernetes configuration in the application.yml, we can benefit of a hot reload configuration mechanism
via Spring application context restart.

The application is totally reactive and no blocking io. It involved: 

* Spring Cloud Gateway instead of Zuul
* Spring WebFlux instead of a classical Spring MVC 
* Spring Data Reactive Mongo instead of Spring Data Mongo 
* WebClient instead of a plain RestTemplate 

Another point of attention may be the usage of Spring Session on Redis for in order to achieve a totally scalable application, without sticky session or something like that.

## How build the project
In this spike I will use minikube like k8s local environment. In order to speed up a fresh minikube instance for this purpose
you can use this command:
```minikube start --vm-driver=virtualbox --cpus 4 --memory 8192 -p spring-cloud-k8s```

Remember to enable ingress with this command: ```minikube addons enable ingress -p spring-cloud-k8s```
In order to test on minikube you can use my docker images on docker hub and that's it install the kubernetes manifests via helm chart under helm folder.
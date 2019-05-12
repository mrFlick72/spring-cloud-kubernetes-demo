# Spring Cloud Kubernetes Hello World

Spring Cloud is an umbrella of projects that help us to build Cloud Native Java application using Spring Ecosystem. 
Here I will show how, using the Spring Cloud Abstraction, it is very easy to switch between specific vendor implementation 
without change one line of code. In particular this application can run on Spring Cloud Netflix and the latest Spring Cloud Kubernetes 
only availing only on profiled Maven/Gradle build and Spring profile.

## The use case

The sample application is a very simple hello world web application. This application is thought for explore service composition, 
load balancing client side and configuration management. The web front end application is composed by two pages under security:
  * web page for the standard user in order to get a special hello with a randomic special message for you, 
  * web page for admin user in order to managing the special message list. 
 
 ### Web Application for simple user screenshot
 ![](https://raw.githubusercontent.com/mrFlick72/spring-cloud-kubernetes-demo/master/images/user_webapp.png)
 
 
 ### Web Application for admin user screenshot
 ![](https://raw.githubusercontent.com/mrFlick72/spring-cloud-kubernetes-demo/master/images/messages_webapp.png)
 
## The technology stack

In this project you can see used many technologies like:

* Spring Cloud Gateway: the reactive Spring counterpart of Zuul, build on Spring 5.x and Webflux
* Spring Cloud Netflix Eureka for Service Discovery 
* Spring Cloud Kubernetes
* Spring Cloud Ribbon 
    * Spring Cloud Kubernetes Ribbon: used in order to achieve client load balancing
    * Spring Cloud Netflix Ribbon: used in order to achieve client load balancing
* Spring Reactive Data Mongo
* Spring WebFlux
* Spring Boot 2.1.x
* Spring Session
* Spring Reactive Security
* Java/Kotlin


## How it works 

Basically on the UI thankful to Spring Cloud Gateway, similarity to Zuul, we can configure a micro proxy between ui-interface and the backend services.  All the magic 
is performed by the yml configuration, even if in any talk on Spring Cloud Gateway probably you can see the java config way in this example I preferred yml config in order to 
can benefit of hot reload route. The configured routes will be useful to get the hello message on the main web application and create or delete special messages on the 
message web application accessed by admin users. the snippet of code that implements this magic is liek below:

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
benefit of the LoadBalancerExchangeFilterFunction injected by spring for us. The configuration is very simple and the usage is a classical rest service call via WebClient:

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
        return webClientBuilder.build().get()
                .uri(helloServiceUri)
                .retrieve()
                .bodyToMono(HashMap.class)
                .flatMap(payload -> just(format(TEMPLATE, name, INSTANCE_ID, payload.getOrDefault("message", DEFAULT_MESSAGE))));
    }
}
```

The application.yml configuration provided via config map for kubernetes profile and application-netflix.yml for netflix profile. The benefit of use configmap 
with kubernetes is that configuring restart actuator endpoint with spring cloud kubernetes configuration in the bootstrap.yml we can benefit of a config hot reload 
via application context restart.

The application is totally reactive and no blocking io, it involved: 

* Spring Cloud Gateway instead of Zuul
* Spring WebFlux instead of a classical Spring MVC 
* Spring Data Reactive Mongo instead of Spring Data Mongo 
* WebClient instead of a plain RestTemplate 

Another point of actention may be the usage of Spring Session
 on Redis for in order to achieve a totally scalable application, without sticky session or something like that.

## How build the project

The magic is behind profiled build and Spring profile. With profiled build, we guarantee that dependencies in the classpath will be correct, 
with spring profile we guarantee that the configuration for spring cloud netflix are enabled only if we want run our application on Spring Cloud Netflix,
while fro kubernetes, in this case, we will use the k8s config map.

Since that we need of Redis for distributed session storage, Mongo as datastore and Eureka for Spring Cloud Netflix the project will provide a docker-compose.yml
capable of provide all for you, The only thing that you have to do is define an .env file with the path for mongo volume. For K8s in the kubernetes subfolder there are all the 
 needed .yaml file.

In order to build the application the commands are: for gradle projects hello-service and message-service: 
gradle build -Pnetflix while for maven ui-interface project mvn clean install -Pnetflix for enable Spring Cloud Netflix and the same command but with -Pkubernetes for kubernetes
 
Kor Spring cloud Netflix, the project provide a sh script file under docker folder called `start.sh`. This script is capable to start all the needed: docker-compose for redis, mongo ed eureka and all the three applications.
 For try the application with Spring Cloud Kubernetes instead, it is possible apply via `kubectl` all the .yml file under docker/kubernetes folder and the application wil be ready to be deployed, of course you can deploy 
 under minikube, the only thing that you should remember is of apply a command like this: `kubectl create clusterrolebinding admin --clusterrole=cluster-admin --serviceaccount=default:default` 
 otherwise you get an error like below: 
```
There was an unexpected error (type=Internal Server Error, status=500).
Error creating bean with name 'ribbonLoadBalancingHttpClient' defined in org.springframework.cloud.netflix.ribbon.apache.HttpClientRibbonConfiguration:
Unsatisfied dependency expressed through method 'ribbonLoadBalancingHttpClient' parameter 2; nested exception is org.springframework.beans.factory.BeanCreationException:
Error creating bean with name 'ribbonLoadBalancer' defined in org.springframework.cloud.netflix.ribbon.RibbonClientConfiguration:
Bean instantiation via factory method failed; nested exception is org.springframework.beans.BeanInstantiationException:
Failed to instantiate [com.netflix.loadbalancer.ILoadBalancer]: 
Factory method 'ribbonLoadBalancer' threw exception; nested exception is io.fabric8.kubernetes.client.KubernetesClientException: 
Failure executing: GET at: https://10.96.0.1/api/v1/namespaces/default/endpoints/message-service. Message: 
Forbidden!Configured service account doesn't have access.
Service account may have been revoked. endpoints "message-service" is forbidden: 
User "system:serviceaccount:default:default" cannot get resource "endpoints" in API group "" in the namespace "default".
```

## Conclusion
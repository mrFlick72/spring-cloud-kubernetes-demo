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
* Spring Boot 2.1.x
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
        public WebClient webClient() {
            return WebClient.builder().build();
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

The application.yml configuration provided via config map for kubernetes profile and application-netflix.yml for netflix profile. The benefit of use configmap 
with kubernetes is that configuring restart actuator endpoint with spring cloud kubernetes configuration in the bootstrap.yml, we can benefit of a hot reload configuration mechanism
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

In order to test on minikube you can use my docker images on docker hub and that's it install the kubernetes manifests under kubernetes folder.

Pay attention before to install all k8s descriptors is needed to apply a command like this: `kubectl create clusterrolebinding admin --clusterrole=cluster-admin --serviceaccount=default:default` 
 The command is needed due to Spring Cloud Kubernetes interacts with Kubernetes api, without run this command you will get an error like below: 
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

Now!, what street choose?, Spring Cloud Netflix or Spring Cloud Kubernetes? Of course the right answer is it depends! 

With Spring Cloud abstraction you can achieve many typical distributed system pattern like: service discovery, client side load balancing and configuration load in a Netflix or Kubernetes environment without 
change one line of code, giving you the possibility to choose later the your way: K8S or not to K8S. Said that, the choice depends form 
requirements, infrastructure already on place and many other concern. The my impression is: very cool the possibility of choose later and test in local, on premise or in the cloud with 
Netflix or on K8s with the assurance that the application behaviour will be near the same, I have particularly appreciated the simple hot reload of application configuration on K8s.
But on the other hands using Spring Cloud Kubernetes for service discovery and client load balancing, that are the main features exposed by Spring Cloud Kubernetes, 
it is an overkill especially considering that those features that are already built in in K8s. 

Moreover considering that the application have to talk with master for applying the api, it can be quite dangerous due to too much knowledge on your application of infrastructure 
and the risk of coupling your application framework with the infrastructure it is a bad thing in my opinion. The real power is choose later not copling for ever to a platform or to a framework. 

At the end if your application run on a public cloud provider use Spring Cloud Netflix can be a very convenient choice otherwise use Kubernetes may be a real popular and farsighted choice, 
especially considering the real cool project pluggable on top of Kubernetes like Istio, Knative and considering that more and more providers are adopting Kubernetes, 
AWS EKS, Google Cloud GKE, Pivotal PKS and many other are an example.

Unfortunately there not exist a correct answer, there exist only use case in wich a choice fit or not. Like in many use case the 
possibility of choice later and fast adopting a new way that is more capable for embrace business changing is a winner choice. 

In this direction, in my opinion, Spring Cloud win due to give us the possibility to choose later if adopting Netflix or Kubernetes at any time and go up and forward in any time.

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
 ![Simple User Web App](/images/user_webapp.png)
 
 
 ### Web Application for admin user screenshot
 ![Admin Web App](/images/messages_webapp.png)
 
## The technology stack

One of the goal of this spike was to show how using the Spring Cloud abstraction in order to switch from Spring Cloud Netflix to Spring Cloud Kubernetes and viceversa, 
without change one line of code. How do that?.

First of all let me the technology involved:

* Spring Cloud Gateway
* Spring Cloud Netflix Eureka for Service Discovery 
* Spring Cloud Kubernetes
* Spring Cloud Ribbon 
** Spring Cloud Kubernetes Ribbon, load balancing client
** Spring Cloud Netflix Ribbon, load balancing client
* Spring Reactive Mongo
* Spring WebFlux
* Spring Boot 2.1.x

The magic is behind profiled build and Spring profile. With profiled build we assurance that the dependencies in the classpath will be correct, 
with spring profile we assurance that the configuration for spring cloud netflix are enabled only if we want run our application on Spring Cloud Netflix,
while fro kubernetes in this case we will use the k8s config map.



## How it works with Spring Cloud Netflix

## How it works with Spring Cloud Kubernetes

## Conclusion
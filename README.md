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
 ![Simple User Web App](/images/messages_webapp.png)
## The technology stack
One of the goal of this spike was to show how using the Spring Cloud abstraction in order to swich from Spring CLoud Netflix to Spring Cloud Kubernetes and viceversa, 
without change one line of code. How do that? 

## How it works with Spring Cloud Netflix

## How it works with Spring Cloud Kubernetes

## Conclusion
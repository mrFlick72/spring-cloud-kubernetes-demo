plugins {
    id "org.springframework.boot" version "2.7.4"
    id "io.spring.dependency-management" version "1.0.14.RELEASE"
    id "java"
}

apply plugin: "io.spring.dependency-management"

group = "it.valeriovaudi"
//version = "0.0.1-SNAPSHOT"
sourceCompatibility = "17"

jar {
    archiveFileName = "hello-service"
}

repositories {
    mavenCentral()
}

ext {
    set("springCloudVersion", "2021.0.4")
}

dependencies {
    implementation "org.springframework.cloud:spring-cloud-starter"

    implementation "org.springframework.cloud:spring-cloud-starter-kubernetes-client-all"
    implementation "org.springframework.cloud:spring-cloud-starter-kubernetes-client-loadbalancer"

    implementation "org.springframework.boot:spring-boot-starter-webflux"
    implementation "org.springframework.boot:spring-boot-starter-actuator"
    
    compile "org.projectlombok:lombok:1.18.6"

    testImplementation "org.springframework.boot:spring-boot-starter-test"
    testImplementation "io.projectreactor:reactor-test"
}

dependencyManagement {
    imports {
        mavenBom "org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}"
    }
}


tasks.named("test") {
    useJUnitPlatform()
}

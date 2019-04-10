package it.valeriovaudi.messageservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.http.HttpStatus.CREATED
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.router

@SpringBootApplication
class MessageServiceApplication

fun main(args: Array<String>) {
    runApplication<MessageServiceApplication>(*args)
}

data class Message(@Id var id: String? = null,
                   var message: String = "")

interface MessageRepository : ReactiveMongoRepository<Message, String>

@Configuration
class MessageRoute(private val messageRepository: MessageRepository) {

    @Bean
    fun route() = router {
        GET("/message-service/message/{messageId}") {
            messageRepository.findById(it.pathVariable("messageId"))
                    .flatMap { ok().body(fromObject(it)) }
        }

        POST("/message-service/message") {
            it.bodyToMono(Message::class.java)
                    .flatMap { messageRepository.save(it) }
                    .flatMap { status(CREATED).build() }
        }

        PUT("/message-service/message/{messageId}") {
            val messageId = it.pathVariable("messageId")
            it.bodyToMono(Message::class.java)
                    .flatMap {
                        it.let {
                            it.id = messageId
                            messageRepository.save(it)
                        }
                    }
                    .flatMap { status(CREATED).build() }
        }
    }
}
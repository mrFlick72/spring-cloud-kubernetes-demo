package it.valeriovaudi.messageservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.web.reactive.function.BodyInserters.fromValue
import org.springframework.web.reactive.function.server.router
import org.springframework.web.util.UriComponentsBuilder
import java.util.*

@SpringBootApplication
class MessageServiceApplication

fun main(args: Array<String>) {
    runApplication<MessageServiceApplication>(*args)
}

data class Message(@Id var id: String? = null, var message: String = "")

interface MessageRepository : ReactiveMongoRepository<Message, String>

@Configuration
class MessageRoute(private val messageRepository: MessageRepository) {

    @Bean
    fun route() = router {

        GET("/message") {
            messageRepository.findAll()
                    .collectList()
                    .flatMap { ok().body(fromValue(it)) }
        }

        GET("/message/random") {
            messageRepository.findAll()
                    .collectList()
                    .flatMap {
                        if (it.size != 0) {
                            ok().body(fromValue(it[Random().nextInt(it.size)]))
                        } else {
                            notFound().build()
                        }
                    }
        }

        GET("/message/{messageId}") {
            messageRepository.findById(it.pathVariable("messageId"))
                    .flatMap { ok().body(fromValue(it)) }
        }

        POST("/message") {
            it.bodyToMono(Message::class.java)
                    .flatMap { messageRepository.save(it) }
                    .flatMap { created(UriComponentsBuilder.fromPath("/message/${it.id}").build().toUri()).build() }
        }

        PUT("/message/{messageId}") {
            val messageId = it.pathVariable("messageId")
            it.bodyToMono(Message::class.java)
                    .flatMap {
                        it.let {
                            it.id = messageId
                            messageRepository.save(it)
                        }
                    }
                    .flatMap { noContent().build() }
        }

        DELETE("/message/{messageId}") {
            val messageId = it.pathVariable("messageId")
            messageRepository.deleteById(messageId)
                    .flatMap { noContent().build() }
        }
    }
}
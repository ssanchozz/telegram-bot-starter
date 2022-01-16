package com.ssanchozzz.rest

import com.ssanchozzz.api.MessagesProcessor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TelegramController(
    private val messagesProcessor: MessagesProcessor,
    @Value("\${telegram.bot.token}") val telegramBotToken: String
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @PostMapping(
        path = ["/{webhookPath}"],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun receiveUpdate(@PathVariable webhookPath: String, @RequestBody update: Update) =
        if (telegramBotToken == webhookPath) {
            log.info("Received an update message to webhook $update")
            messagesProcessor.process(update)
        } else {
            log.warn("Path $webhookPath is not available for processing")
        }

    @GetMapping("/")
    fun ok(): String = "OK"
}

package com.ssanchozzz.telebreak.rest

import com.ssanchozzz.telebreak.api.MessagesProcessor
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TelegramController(
    private val messagesProcessor: MessagesProcessor
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    companion object {
        const val webhookPath = "update"
    }

    // TODO path should be a token, but need to be able to setup it in runtime
    @PostMapping(
        path = [webhookPath],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun receiveUpdate(@RequestBody update: Update) {
        log.info("Received an update message to webhook $update")
        messagesProcessor.process(update)
    }

    @GetMapping("/")
    fun ok(): String = "OK"
}

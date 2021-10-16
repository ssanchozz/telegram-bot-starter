package com.ssanchozzz.telebreak.rest

import com.ssanchozzz.telebreak.domain.BreakCalculator
import com.ssanchozzz.telebreak.domain.Message
import com.ssanchozzz.telebreak.domain.MessageResponse
import com.ssanchozzz.telebreak.domain.UpdateResponse
import com.ssanchozzz.telebreak.domain.UserResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpMethod.GET
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class TelegramApi(
        @Value("\${telegram.bot.url}") val url: String,
        @Value("\${telegram.bot.token}") val token: String,
        val breakCalculator: BreakCalculator
) : InitializingBean {

    private val log = LoggerFactory.getLogger(this::class.java)

    private val restTemplate = RestTemplateBuilder().build()
    private var offset: Int = 0

    override fun afterPropertiesSet() {
        getMe()
        log.info("API initialized successfully!")
        getUpdates()
    }

    private fun getMe() {
        val response = restTemplate.exchange("$url$token/getMe", GET, null, UserResponse::class.java)
        val user = response.body!!.result
        log.debug(user.toString())
    }

    private fun getUpdates(): Unit? = try {
        log.info("Making a request to $url with offset $offset")

        val response = restTemplate.exchange(
                "$url$token/getUpdates?offset=$offset",
                GET,
                null,
                UpdateResponse::class.java
        )
        log.info("Got a response from $url, status code ${response.statusCode}")

        log.debug(response.toString())
        val result = response.body!!.result
        result.forEach {
            val message = it.message ?: return@forEach

            log.info("Received a message, processing: $message")

            val perifCommand = "/perif"
            if (message.text == null) {
                return@forEach
            }
            if (message.text.startsWith(perifCommand)) {
                processPerifCommand(perifCommand, message)
            } else if (message.text.startsWith("/")) {
                log.info("Unknown command $message")
            }
        }

        result.lastOrNull()?.let { update -> offset = update.id + 1 }

    } finally {
        getUpdates()
    }

    private fun processPerifCommand(perifCommand: String, message: Message) {
        log.info("Processing $perifCommand command")
        message.chat?.let { chat ->
            try {
                sendMessage(
                    chat.id,
                    getResponseMessage(message, perifCommand)
                ).let { sentMessage ->
                    log.debug(sentMessage.toString())
                }
                log.info("$perifCommand command processed successfully")
            } catch (e: Exception) {
                log.error("An exception occurred while processing command $perifCommand", e)
            }
        }
    }

    private fun getResponseMessage(message: Message, prefix: String): String = message.let {
        val text = message.text!!
        if (text.length > prefix.length) {
            val substring = text.substring(prefix.length + 1, prefix.length + 17)
            val dateTimeToCheck = LocalDateTime.parse(substring, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            breakCalculator.getClosestBreakMessage(dateTimeToCheck)
        } else {
            breakCalculator.getClosestBreakMessage(message.date)
        }
    }

    private fun sendMessage(chatId: Int, message: String) = restTemplate.exchange(
            "$url$token/sendMessage?chat_id=$chatId&text=$message",
            GET,
            null,
            MessageResponse::class.java
    )

    private fun setWebhook() {
        TODO("Need to user webhooks instead of long polling in PROD")
    }
}
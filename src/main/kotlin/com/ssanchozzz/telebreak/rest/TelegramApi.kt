package com.ssanchozzz.telebreak.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.ssanchozzz.telebreak.domain.BreakCalculator
import com.ssanchozzz.telebreak.domain.Command
import com.ssanchozzz.telebreak.domain.Message
import com.ssanchozzz.telebreak.domain.MessageResponse
import com.ssanchozzz.telebreak.domain.Update
import com.ssanchozzz.telebreak.domain.UpdateResponse
import com.ssanchozzz.telebreak.domain.UserResponse
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod.GET
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Collections


@Component
class TelegramApi(
    @Value("\${telegram.bot.url}") val url: String,
    @Value("\${telegram.bot.token}") val token: String,
    @Value("\${telegram.bot.protocol}") val protocol: String,
    @Value("\${telegram.bot.webhook}") val webhookUrl: String,
    val breakCalculator: BreakCalculator
) : InitializingBean {

    private val log = LoggerFactory.getLogger(this::class.java)

    val chatsList: MutableList<Int> = Collections.synchronizedList(mutableListOf())

    private val perifCommand = "/perif"

    private val restTemplate = RestTemplateBuilder()
        .setConnectTimeout(Duration.of(60, ChronoUnit.SECONDS))
        .setReadTimeout(Duration.of(60, ChronoUnit.SECONDS))
        .build()

    private val objectMapper = ObjectMapper()

    private var offset: Int = 0

    override fun afterPropertiesSet() {
        getMe()
        setMyCommands()

        when (protocol) {
            "webhook" -> {
                log.info("Registering a webhook")
                setWebhook("https://$webhookUrl/${TelegramController.webhookPath}")
            }
            "longpolling" -> {
                log.info("Deleting webhook")
                deleteWebhook()
                log.info("Starting long polling")
                runBlocking(newSingleThreadContext("Long polling thread")) {
                    startGettingUpdates()
                }
            }
            else -> log.error(
                "Unknown protocol, bot will do nothing! Please setup telegram.bot.protocol to either longpolling or webhook"
            )
        }

        log.info("API initialized successfully!")
    }

    private fun setWebhook(webhookUrl: String) {
        restTemplate.exchange(
            "$url$token/setWebhook?url=$webhookUrl",
            GET,
            null,
            object : ParameterizedTypeReference<HashMap<String, String>>() {}
        )
    }

    private fun deleteWebhook() {
        restTemplate.exchange(
            "$url$token/deleteWebhook",
            GET,
            null,
            object : ParameterizedTypeReference<HashMap<String, String>>() {}
        )
    }

    private fun getMe() {
        val response = restTemplate.exchange("$url$token/getMe", GET, null, UserResponse::class.java)
        val user = response.body!!.result
        log.debug(user.toString())
    }

    private fun setMyCommands() {
        val commands = listOf(
            Command("perif", "perif")
        )

        val jsonCommands = objectMapper.writeValueAsString(commands)

        restTemplate.exchange(
            "$url$token/setMyCommands?commands=$jsonCommands",
            GET,
            null,
            object : ParameterizedTypeReference<HashMap<String, String>>() {}
        )
    }

    private fun startGettingUpdates(): Unit? {
        while (true) {
            try {
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
                result.forEach { update ->
                    processUpdate(update)
                }

                result.lastOrNull()?.let { update -> offset = update.id + 1 }
            } catch (e: Exception) {
                log.info("Failed to process request", e)
            }
        }
    }

    internal fun processWebhookUpdate(update: Update) {
        processUpdate(update)
        offset = update.id + 1
    }

    private fun processUpdate(update: Update) {
        val message = update.message ?: return

        message.chat?.let {
            chatsList.add(it.id)
        }

        log.info("Received a message, processing: $message")

        if (message.text == null) {
            return
        }

        val text = message.text

        if (text.startsWith(perifCommand)) {
            processPerifCommand(message)
        } else if (text.startsWith("/")) {
            log.info("Unknown command $message")
        }
    }

    private fun processPerifCommand(message: Message) {
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

    fun sendMessage(chatId: Int, message: String) = restTemplate.exchange(
        "$url$token/sendMessage?chat_id=$chatId&text=$message",
        GET,
        null,
        MessageResponse::class.java
    )
}
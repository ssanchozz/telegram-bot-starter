package com.ssanchozzz.rest

import com.ssanchozzz.api.TelegramApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class BotStarter(
    private val telegramApi: TelegramApi,
    private val messagesProcessor: MessagesProcessor,
    @Value("\${telegram.bot.protocol}") val protocol: String,
    @Value("\${telegram.bot.webhook}") val webhookUrl: String,
    @Value("\${telegram.bot.token}") val telegramBotToken: String
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun startBot() = runBlocking {
        when (protocol) {
            "webhook" -> {
                log.info("Registering a webhook")
                telegramApi.setWebhook("https://$webhookUrl/${telegramBotToken}")
            }
            "longpolling" -> {
                log.info("Deleting webhook")
                telegramApi.deleteWebhook()
                log.info("Starting long polling")
                launch {
                    startGettingUpdates()
                }
            }
            else -> log.error(
                "Unknown protocol, bot will do nothing! " +
                        "Please setup telegram.bot.protocol to either longpolling or webhook"
            )
        }
        log.info("Message processor initialization completed successfully")
    }

    private fun startGettingUpdates() {
        while (true) {
            try {
                val updates = telegramApi.getUpdates(messagesProcessor.offset.get())
                updates?.forEach { update ->
                    messagesProcessor.process(update)
                }
            } catch (e: Exception) {
                log.info("Failed to process request", e)
            }
        }
    }
}

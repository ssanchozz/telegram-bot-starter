package com.ssanchozzz.telebreak.rest

import com.ssanchozzz.telebreak.api.MessagesProcessor
import com.ssanchozzz.telebreak.api.TelegramApi
import com.ssanchozzz.telebreak.domain.BreakCalculator
import com.ssanchozzz.telebreak.rest.helper.Commands.perifCommand
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Collections

@Component
class MessagesProcessor(
    private val telegramApi: TelegramApi,
    val breakCalculator: BreakCalculator,
    @Value("\${telegram.bot.protocol}") val protocol: String,
    @Value("\${telegram.bot.webhook}") val webhookUrl: String
) : MessagesProcessor, InitializingBean {

    private val log = LoggerFactory.getLogger(this::class.java)

    val chatsList: MutableSet<Int> = Collections.synchronizedSet(mutableSetOf())

    private var offset: Int = 0

    override fun afterPropertiesSet() = runBlocking {
        when (protocol) {
            "webhook" -> {
                log.info("Registering a webhook")
                telegramApi.setWebhook("https://$webhookUrl/${TelegramController.webhookPath}")
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
                "Unknown protocol, bot will do nothing! Please setup telegram.bot.protocol to either longpolling or webhook"
            )
        }
        log.info("Message processor initialization completed successfully")
    }

    private fun startGettingUpdates(): Unit? {
        while (true) {
            try {
                val updates = telegramApi.getUpdates(offset)
                updates?.forEach { update ->
                    processUpdate(update)
                }

                updates?.lastOrNull()?.let { update -> offset = update.id + 1 }
            } catch (e: Exception) {
                log.info("Failed to process request", e)
            }
        }
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

        if (text!!.startsWith(perifCommand.command)) {
            processPerifCommand(message)
        } else if (text.startsWith("/")) {
            log.info("Unknown command $message")
        }
    }

    private fun processPerifCommand(message: Message) {
        log.info("Processing $perifCommand command")
        message.chat?.let { chat ->
            try {
                telegramApi.sendMessage(
                    chat.id,
                    getResponseMessage(message, perifCommand.command)
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

    override fun process(update: Update) {
        processUpdate(update)
        offset = update.id + 1
    }
}

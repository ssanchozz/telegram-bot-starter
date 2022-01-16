package com.ssanchozzz.rest

import com.ssanchozzz.api.MessagesProcessor
import com.ssanchozzz.api.TelegramApi
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicInteger

@Component
class MessagesProcessor(
    private val telegramApi: TelegramApi
) : MessagesProcessor {

    private val log = LoggerFactory.getLogger(this::class.java)

    var offset = AtomicInteger(0)
    private val helloCommand = "/Hello"

    override fun process(update: Update) {
        processUpdate(update)
        offset.incrementAndGet()
    }

    private fun processUpdate(update: Update) {
        val message = update.message ?: return

        log.info("Received a message, processing: $message")

        if (message.text == null) {
            return
        }

        val text = message.text

        if (text!!.startsWith(helloCommand)) {
            message.chat?.let { chat ->
                try {
                    telegramApi.sendMessage(
                        chat.id,
                        "World!"
                    ).let { sentMessage ->
                        log.debug(sentMessage.toString())
                    }
                    log.info("$helloCommand command processed successfully")
                } catch (e: Exception) {
                    log.error("An exception occurred while processing command $helloCommand", e)
                }
            }
        } else if (text.startsWith("/")) {
            log.info("Unknown command $message")
        }
    }
}

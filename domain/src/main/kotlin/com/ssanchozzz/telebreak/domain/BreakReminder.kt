package com.ssanchozzz.telebreak.domain

import com.ssanchozzz.telebreak.api.TelegramApi
import com.ssanchozzz.telebreak.rest.MessagesProcessor
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@EnableScheduling
class BreakReminder(
    private val breakCalculator: BreakCalculator,
    private val telegramApi: TelegramApi,
    private val messagesProcessor: MessagesProcessor
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    internal val chatWasNotified: MutableMap<Int, Boolean> = mutableMapOf()

    @Scheduled(fixedDelay = 1000)
    internal fun notifyChats() {
        messagesProcessor.chatsList.forEach {
            chatWasNotified.computeIfAbsent(it) { false }
        }

        chatWasNotified.forEach { chatIdToNotify ->
            val chatId = chatIdToNotify.key
            val wasNotified = chatIdToNotify.value
            val breakNow = breakCalculator.isBreakNow()

            if (breakNow && !wasNotified) {
                log.info("Sending break a notification to $chatId")
                telegramApi.sendMessage(chatId, "It is a break now!")
                chatWasNotified[chatId] = true
            } else if (!breakNow && wasNotified) {
                log.info("Setting notified to false for $chatId")
                chatWasNotified[chatId] = false
            }
        }
    }
}

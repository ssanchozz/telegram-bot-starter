package com.ssanchozzz.telebreak.domain

import com.ssanchozzz.telebreak.rest.TelegramApi
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneId

@Component
@EnableScheduling
class BreakReminder(
    private val breakCalculator: BreakCalculator,
    private val telegramApi: TelegramApi,
    @Value("\${telegram.bot.timezone}") val zone: String,
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    private val chatWasNotified: MutableMap<Int, Boolean> = mutableMapOf()

    private fun isBreakNow(): Boolean {
        val currentDateTime = LocalDateTime.now(ZoneId.of(zone))
        val state = breakCalculator.getState(currentDateTime)
        return state == BreakCalculator.State.IS_BREAK_NOW
    }

    @Scheduled(fixedDelay = 1000)
    private fun notifyChats() {
        telegramApi.chatsList.forEach {
            chatWasNotified.computeIfAbsent(it) { false }
        }

        chatWasNotified.forEach { chatIdToNotified ->
            val chatId = chatIdToNotified.key
            val wasNotified = chatIdToNotified.value
            val breakNow = isBreakNow()

            if (breakNow && !wasNotified) {
                log.info("Sending break a notification to $chatId")
                telegramApi.sendMessage(chatId, "It's break now!")
                chatWasNotified[chatId] = true
            } else {
                chatWasNotified[chatId] = false
            }
        }
    }
}
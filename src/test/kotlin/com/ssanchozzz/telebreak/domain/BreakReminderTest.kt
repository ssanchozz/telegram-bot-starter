package com.ssanchozzz.telebreak.domain

import com.ssanchozzz.telebreak.rest.MessagesProcessor
import com.ssanchozzz.telebreak.rest.TelegramApi
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class BreakReminderTest {

    private val telegramApi = mockk<TelegramApi>(relaxed = true)

    private val breakCalculator = mockk<BreakCalculator>(relaxed = true)

    private val messagesProcessor = MessagesProcessor(telegramApi, breakCalculator, "", "")

    private val breakReminder = BreakReminder(
        breakCalculator,
        telegramApi,
        messagesProcessor
    )

    @BeforeEach
    fun initialize() {
    }

    @Test
    fun noChatsCheck() {
        breakReminder.notifyChats()
    }

    @Test
    fun checkFirstInvocationSendsNotificationSecondDoesnt() {
        val chatId = 123
        messagesProcessor.chatsList.add(chatId)

        every { breakCalculator.isBreakNow() }.returns(true)
        breakReminder.notifyChats()
        verify { telegramApi.sendMessage(123, "It's break now!") }
        Assertions.assertTrue(breakReminder.chatWasNotified[chatId]!!)

        every { breakCalculator.isBreakNow() }.returns(false)
        breakReminder.notifyChats()
        Assertions.assertFalse(breakReminder.chatWasNotified[chatId]!!)
    }
}
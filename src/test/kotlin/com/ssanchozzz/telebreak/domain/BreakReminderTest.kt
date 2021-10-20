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
    fun `Check that if the first invocation sends a notification, the second doesn't`() {
        val chatId = 123
        messagesProcessor.chatsList.add(chatId)

        every { breakCalculator.isBreakNow() }.returns(true)
        breakReminder.notifyChats()
        verify { telegramApi.sendMessage(123, "It is a break now!") }
        Assertions.assertTrue(breakReminder.chatWasNotified[chatId]!!)

        breakReminder.notifyChats()
        Assertions.assertTrue(breakReminder.chatWasNotified[chatId]!!)

        every { breakCalculator.isBreakNow() }.returns(false)
        breakReminder.notifyChats()
        Assertions.assertFalse(breakReminder.chatWasNotified[chatId]!!)

        verify(exactly = 1) { telegramApi.sendMessage(123, "It is a break now!") }
    }

    @Test
    fun `Check that if add the same chat id, message to the chat is sent only once`() {
        val chatId = 123
        messagesProcessor.chatsList.add(chatId)
        every { breakCalculator.isBreakNow() }.returns(true)

        breakReminder.notifyChats()
        messagesProcessor.chatsList.add(chatId)
        breakReminder.notifyChats()
        messagesProcessor.chatsList.add(chatId)
        messagesProcessor.chatsList.add(chatId)
        breakReminder.notifyChats()

        Assertions.assertEquals(1, messagesProcessor.chatsList.size)
        Assertions.assertEquals(1, breakReminder.chatWasNotified.size)
        verify(exactly = 1) { telegramApi.sendMessage(123, "It is a break now!") }
    }
}
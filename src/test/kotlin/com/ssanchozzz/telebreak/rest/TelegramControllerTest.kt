package com.ssanchozzz.telebreak.rest

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDateTime.of

internal class TelegramControllerTest {

    private val tc = TelegramController()

    @Test
    fun test() {
        Assertions.assertEquals(
            "Classes not yet started",
            tc.getClosestBreak(of(2021, 10, 7, 8, 0))
        )
    }
}
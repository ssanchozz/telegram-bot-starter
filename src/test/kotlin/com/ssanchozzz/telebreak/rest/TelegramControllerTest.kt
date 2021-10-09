package com.ssanchozzz.telebreak.rest

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDateTime.of

internal class TelegramControllerTest {

    private val tc = TelegramController()

    @Test
    fun testClassesNotYetStarted() {
        Assertions.assertEquals(
                "Classes not yet started",
                tc.getClosestBreak(of(2021, 10, 7, 8, 0))
        )
    }

    @Test
    internal fun testNoClassesToday() {
        Assertions.assertEquals(
                "No classes today",
                tc.getClosestBreak(of(2021, 10, 9, 8, 0))
        )
    }

    @Test
    internal fun testClassesWereFinishedForToday() {
        Assertions.assertEquals(
                "Classes were finished for today",
                tc.getClosestBreak(of(2021, 10, 7, 17, 0))
        )
    }

    @Test
    internal fun testNextBreakFirstBuildingSchedule() {
        Assertions.assertEquals(
                "Next break is in 15 minutes",
                tc.getClosestBreak(of(2021, 10, 7, 9, 30))
        )
    }

    @Test
    internal fun testNextBreakSecondBuildingSchedule() {
        Assertions.assertEquals(
                "Next break is in 40 minutes",
                tc.getClosestBreak(of(2021, 10, 6, 9, 30))
        )
    }

    @Test
    internal fun testIsBreakNowSecondBuildingSchedule() {
        Assertions.assertEquals(
                "It's a break now!",
                tc.getClosestBreak(of(2021, 10, 6, 9, 20))
        )
    }
}
package com.ssanchozzz.telebreak.rest

import com.ssanchozzz.telebreak.domain.BreakCalculator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDateTime.of

internal class TelegramControllerTest {

    private val bc = BreakCalculator()

    @Test
    fun testClassesNotYetStarted() {
        Assertions.assertEquals(
                "Classes not yet started",
                bc.getClosestBreakMessage(of(2021, 10, 7, 8, 0))
        )
    }

    @Test
    internal fun testNoClassesToday() {
        Assertions.assertEquals(
                "No classes today",
                bc.getClosestBreakMessage(of(2021, 10, 9, 8, 0))
        )
    }

    @Test
    internal fun testClassesWereFinishedForToday() {
        Assertions.assertEquals(
                "Classes were finished for today",
                bc.getClosestBreakMessage(of(2021, 10, 7, 17, 0))
        )
    }

    @Test
    internal fun testNextBreakFirstBuildingSchedule() {
        Assertions.assertEquals(
                "Next break is in 15 minutes",
                bc.getClosestBreakMessage(of(2021, 10, 7, 9, 30))
        )
    }

    @Test
    internal fun testNextBreakSecondBuildingSchedule() {
        Assertions.assertEquals(
                "Next break is in 40 minutes",
                bc.getClosestBreakMessage(of(2021, 10, 6, 9, 30))
        )
    }

    @Test
    internal fun testIsBreakNowSecondBuildingSchedule() {
        Assertions.assertEquals(
                "It's a break now!",
                bc.getClosestBreakMessage(of(2021, 10, 6, 9, 20))
        )
    }
}
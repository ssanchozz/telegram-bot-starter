package com.ssanchozzz.telebreak.rest

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.DayOfWeek.*
import java.time.LocalDateTime
import java.time.LocalTime.*
import java.time.temporal.ChronoUnit.MINUTES

@RestController
class TelegramController {

    private val classDurationMinutes = 45L

    private val secondBuildingSchedule = listOf(
        of(8, 30),
        of(9, 25),
        of(10, 30),
        of(11, 25),
        of(12, 30),
        of(13, 25),
        of(14, 20),
        of(15, 25)
    )

    private val firstBuildingSchedule = listOf(
        of(9, 0),
        of(9, 55),
        of(11, 0),
        of(11, 55),
        of(13, 55),
        of(14, 50),
        of(15, 45)
    )

    private val schedules = mapOf(
        MONDAY to secondBuildingSchedule,
        TUESDAY to firstBuildingSchedule,
        WEDNESDAY to secondBuildingSchedule,
        THURSDAY to firstBuildingSchedule
    )

    @GetMapping("closestBreak")
    fun getClosestBreak(): String {
        val now = LocalDateTime.now()

        return getClosestBreak(now)
    }

    internal fun getClosestBreak(referenceDateTime: LocalDateTime): String {
        if (setOf(FRIDAY, SATURDAY, SUNDAY).contains(referenceDateTime.dayOfWeek)) {
            return "No classes today"
        }

        val schedule = schedules[referenceDateTime.dayOfWeek]!!
        val found = schedule.binarySearch(referenceDateTime.toLocalTime())
        if (found < 0 || found == 0
            && MINUTES.between(referenceDateTime, schedule.first()) > classDurationMinutes
        ) {
            return "Classes not yet started"
        } else if (found == schedule.size
            && MINUTES.between(schedule.last(), referenceDateTime) > classDurationMinutes
        ) {
            return "Classes were finished for today"
        }

        val previousClassStart = schedule[found - 1]
        if (MINUTES.between(previousClassStart, referenceDateTime) < classDurationMinutes) {
            return "Next break in ${classDurationMinutes - MINUTES.between(previousClassStart, referenceDateTime)} minutes"
        }

        return "It's a break now!"
    }
}
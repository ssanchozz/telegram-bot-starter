package com.ssanchozzz.telebreak.domain

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.DayOfWeek.FRIDAY
import java.time.DayOfWeek.MONDAY
import java.time.DayOfWeek.SATURDAY
import java.time.DayOfWeek.SUNDAY
import java.time.DayOfWeek.THURSDAY
import java.time.DayOfWeek.TUESDAY
import java.time.DayOfWeek.WEDNESDAY
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime.of
import java.time.ZoneId
import java.time.temporal.ChronoUnit.MINUTES

@Component
class BreakCalculator(
    @Value("\${telegram.bot.timezone}") val zone: String,
) {

    private val classDurationMinutes = 45L

    private val firstBuildingSchedule = listOf(
        of(9, 0),
        of(9, 55),
        of(11, 0),
        of(11, 55),
        of(13, 55),
        of(14, 50),
        of(15, 45)
    )

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

    private val schedules = mapOf(
        MONDAY to secondBuildingSchedule,
        TUESDAY to firstBuildingSchedule,
        WEDNESDAY to secondBuildingSchedule,
        THURSDAY to firstBuildingSchedule
    )

    fun getClosestBreakMessage(dateTime: Int): String = getClosestBreakMessage(
        LocalDateTime.ofInstant(Instant.ofEpochSecond(dateTime.toLong()), ZoneId.of(zone))
    )

    private data class Result(
        val state: State,
        val message: String,
        val messageParams: List<String> = listOf()
    )

    enum class State {
        IS_BREAK_NOW,
        NO_CLASSES,
        CLASSES_NOT_STARTED,
        CLASSES_FINISHED,
        CLASS
    }

    private fun getResult(referenceDateTime: LocalDateTime): Result {
        if (setOf(FRIDAY, SATURDAY, SUNDAY).contains(referenceDateTime.dayOfWeek)) {
            return Result(
                State.NO_CLASSES,
                "No classes today"
            )
        }

        val schedule = schedules[referenceDateTime.dayOfWeek]!!
        val searchResult = schedule.binarySearch(referenceDateTime.toLocalTime())
        val insertionPoint = if (searchResult >= 0) searchResult else -(searchResult + 1)
        if (insertionPoint == 0) {
            return Result(
                State.CLASSES_NOT_STARTED,
                "Classes not yet started"
            )
        } else if (insertionPoint == schedule.size &&
            MINUTES.between(schedule.last(), referenceDateTime) > classDurationMinutes
        ) {
            return Result(
                State.CLASSES_FINISHED,
                "Classes were finished for today"
            )
        }

        val previousClassStart = schedule[insertionPoint - 1]
        if (MINUTES.between(previousClassStart, referenceDateTime) < classDurationMinutes) {
            return Result(
                State.CLASS,
                "It's class now, next break is in %s minutes",
                listOf(
                    (
                        classDurationMinutes - MINUTES.between(
                            previousClassStart,
                            referenceDateTime
                        )
                        ).toInt().toString()
                )
            )
        }

        return Result(
            State.IS_BREAK_NOW,
            "It's a break now!"
        )
    }

    internal fun getState(referenceDateTime: LocalDateTime): State = getResult(referenceDateTime).state

    internal fun isBreakNow(): Boolean {
        val currentDateTime = LocalDateTime.now(ZoneId.of(zone))
        val state = getState(currentDateTime)
        return state == State.IS_BREAK_NOW
    }

    internal fun getClosestBreakMessage(referenceDateTime: LocalDateTime): String {
        val result = getResult(referenceDateTime)
        return if (result.messageParams.isEmpty()) {
            result.message
        } else {
            String.format(result.message, *result.messageParams.toTypedArray())
        }
    }
}

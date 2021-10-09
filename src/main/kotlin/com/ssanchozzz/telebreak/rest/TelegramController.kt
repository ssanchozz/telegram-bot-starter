package com.ssanchozzz.telebreak.rest

import com.ssanchozzz.telebreak.domain.BreakCalculator
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.DayOfWeek.*
import java.time.LocalDateTime
import java.time.LocalTime.*
import java.time.temporal.ChronoUnit.MINUTES

@RestController
class TelegramController(
        private val breakCalculator: BreakCalculator
) {
    @GetMapping("closestBreak")
    fun getClosestBreak(): String = breakCalculator.getClosestBreakMessage()
}
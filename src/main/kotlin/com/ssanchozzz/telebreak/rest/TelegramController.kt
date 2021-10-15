package com.ssanchozzz.telebreak.rest

import com.ssanchozzz.telebreak.domain.BreakCalculator
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
class TelegramController(
        private val breakCalculator: BreakCalculator
) {
    @GetMapping("closestBreak")
    fun getClosestBreak(): String = breakCalculator.getClosestBreakMessage(LocalDateTime.now())
}
package com.ssanchozzz.telebreak.rest.helper

import com.ssanchozzz.telebreak.domain.Command

object Commands {
    val perifCommand = Command("perif", "Returns a next when is the next perif")
}

fun Command.getPreSlashedString(): String = "/$command"
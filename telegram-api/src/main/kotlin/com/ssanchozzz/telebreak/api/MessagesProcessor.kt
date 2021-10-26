package com.ssanchozzz.telebreak.api

import com.ssanchozzz.telebreak.rest.Update

interface MessagesProcessor {

    fun process(update: Update)
}

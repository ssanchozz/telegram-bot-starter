package com.ssanchozzz.api

import com.ssanchozzz.rest.Update

interface MessagesProcessor {

    fun process(update: Update)
}

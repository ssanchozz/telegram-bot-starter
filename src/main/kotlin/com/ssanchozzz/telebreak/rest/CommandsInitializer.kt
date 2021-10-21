package com.ssanchozzz.telebreak.rest

import com.ssanchozzz.telebreak.rest.helper.Commands.perifCommand
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Component

@Component
class CommandsInitializer(
    private val telegramApi: TelegramApi
) : InitializingBean {

    private val log = LoggerFactory.getLogger(this::class.java)

    private val commands = listOf(
        perifCommand
    )

    override fun afterPropertiesSet() {
        telegramApi.getMe()
        telegramApi.setMyCommands(commands)
        log.info("API and commands initialized successfully!")
    }
}

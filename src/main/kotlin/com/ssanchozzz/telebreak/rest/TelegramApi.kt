package com.ssanchozzz.telebreak.rest

import com.ssanchozzz.telebreak.domain.Command
import com.ssanchozzz.telebreak.domain.Message
import com.ssanchozzz.telebreak.domain.Update
import com.ssanchozzz.telebreak.domain.User
import com.ssanchozzz.telebreak.rest.helper.JsonHelper.objectMapper
import com.ssanchozzz.telebreak.rest.helper.RestHelper.invokeGet
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component


@Component
class TelegramApi(
    @Value("\${telegram.bot.url}") val url: String,
    @Value("\${telegram.bot.token}") val token: String
) {

    internal fun getMe() = invokeGet<User>("$url$token/getMe")

    internal fun setMyCommands(commands: List<Command>) {
        val jsonCommands = objectMapper.writeValueAsString(commands)
        invokeGet<Boolean>("$url$token/setMyCommands?commands=$jsonCommands")
    }

    internal fun getUpdates(offset: Int) = invokeGet<List<Update>>("$url$token/getUpdates?offset=$offset")

    internal fun setWebhook(webhookUrl: String) = invokeGet<Boolean>("$url$token/setWebhook?url=$webhookUrl")

    internal fun deleteWebhook() = invokeGet<Boolean>("$url$token/deleteWebhook")

    fun sendMessage(chatId: Int, message: String) = invokeGet<Message>(
        "$url$token/sendMessage?chat_id=$chatId&text=$message"
    )
}
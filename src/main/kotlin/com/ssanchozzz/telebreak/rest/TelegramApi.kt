package com.ssanchozzz.telebreak.rest

import com.ssanchozzz.telebreak.domain.Command
import com.ssanchozzz.telebreak.domain.Message
import com.ssanchozzz.telebreak.domain.TelegramApiResponse
import com.ssanchozzz.telebreak.domain.Update
import com.ssanchozzz.telebreak.domain.User
import com.ssanchozzz.telebreak.rest.helper.RestHelper.invokeGet
import com.ssanchozzz.telebreak.rest.helper.RestHelper.invokePost
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component

@Component
class TelegramApi(
    @Value("\${telegram.bot.url}") val url: String,
    @Value("\${telegram.bot.token}") val token: String
) {

    internal fun getMe() =
        invokeGet("$url$token/getMe", object : ParameterizedTypeReference<TelegramApiResponse<User>>() {})

    internal fun setMyCommands(commands: List<Command>) = invokePost(
        "$url$token/setMyCommands",
        object : ParameterizedTypeReference<TelegramApiResponse<Boolean>>() {}, commands
    )

    internal fun getUpdates(offset: Int) = invokeGet(
        "$url$token/getUpdates?offset=$offset",
        object : ParameterizedTypeReference<TelegramApiResponse<List<Update>>>() {}
    )

    internal fun setWebhook(webhookUrl: String) = invokeGet(
        "$url$token/setWebhook?url=$webhookUrl",
        object : ParameterizedTypeReference<TelegramApiResponse<Boolean>>() {}
    )

    internal fun deleteWebhook() = invokeGet(
        "$url$token/deleteWebhook",
        object : ParameterizedTypeReference<TelegramApiResponse<Boolean>>() {}
    )

    fun sendMessage(chatId: Int, message: String) = invokeGet(
        "$url$token/sendMessage?chat_id=$chatId&text=$message",
        object : ParameterizedTypeReference<TelegramApiResponse<Message>>() {}
    )
}

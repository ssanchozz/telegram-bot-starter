package com.ssanchozzz.telebreak.api

import com.ssanchozzz.telebreak.rest.Command
import com.ssanchozzz.telebreak.rest.Message
import com.ssanchozzz.telebreak.rest.RestHelper.invokeGet
import com.ssanchozzz.telebreak.rest.RestHelper.invokePost
import com.ssanchozzz.telebreak.rest.TelegramApiResponse
import com.ssanchozzz.telebreak.rest.Update
import com.ssanchozzz.telebreak.rest.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component

@Component
class TelegramApi(
    @Value("\${telegram.bot.url}") val url: String,
    @Value("\${telegram.bot.token}") val token: String
) {

    fun getMe() =
        invokeGet("$url$token/getMe", object : ParameterizedTypeReference<TelegramApiResponse<User>>() {})

    fun setMyCommands(commands: List<Command>) = invokePost(
        "$url$token/setMyCommands",
        object : ParameterizedTypeReference<TelegramApiResponse<Boolean>>() {}, commands
    )

    fun getUpdates(offset: Int) = invokeGet(
        "$url$token/getUpdates?offset=$offset",
        object : ParameterizedTypeReference<TelegramApiResponse<List<Update>>>() {}
    )

    fun setWebhook(webhookUrl: String) = invokeGet(
        "$url$token/setWebhook?url=$webhookUrl",
        object : ParameterizedTypeReference<TelegramApiResponse<Boolean>>() {}
    )

    fun deleteWebhook() = invokeGet(
        "$url$token/deleteWebhook",
        object : ParameterizedTypeReference<TelegramApiResponse<Boolean>>() {}
    )

    fun sendMessage(chatId: Int, message: String) = invokeGet(
        "$url$token/sendMessage?chat_id=$chatId&text=$message",
        object : ParameterizedTypeReference<TelegramApiResponse<Message>>() {}
    )
}

package com.ssanchozzz.api

import com.ssanchozzz.rest.Command
import com.ssanchozzz.rest.Message
import com.ssanchozzz.rest.RestHelper.invokeGet
import com.ssanchozzz.rest.RestHelper.invokePost
import com.ssanchozzz.rest.TelegramApiResponse
import com.ssanchozzz.rest.Update
import com.ssanchozzz.rest.User
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

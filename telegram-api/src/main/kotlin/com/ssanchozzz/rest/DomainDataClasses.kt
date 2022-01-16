package com.ssanchozzz.rest

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class TelegramApiResponse<T>(
    @JsonProperty("ok") val ok: Boolean,
    @JsonProperty("result") val result: T
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class User(
    @JsonProperty("id") val id: Int,
    @JsonProperty("is_bot") val isBot: Boolean,
    @JsonProperty("first_name") val firstName: String,
    @JsonProperty("last_name") val lastName: String?,
    @JsonProperty("username") val userName: String?,
    @JsonProperty("language_code") val languageCode: String?,
    @JsonProperty("can_join_groups") val canJoinGroups: Boolean?,
    @JsonProperty("can_read_all_group_messages") val canReadAllGroupMessages: Boolean?,
    @JsonProperty("supports_inline_queries") val supportsInlineQueries: Boolean?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Chat(
    @JsonProperty("id") val id: Int,
    @JsonProperty("first_name") val firstName: String?,
    @JsonProperty("last_name") val lastName: String?,
    @JsonProperty("username") val username: String?,
    @JsonProperty("type") val type: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Update(
    @JsonProperty("update_id") val id: Int,
    @JsonProperty("message") val message: Message?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Message(
    @JsonProperty("message_id") val id: Int,
    @JsonProperty("from") val user: User,
    @JsonProperty("chat") val chat: Chat?,
    @JsonProperty("date") val date: Int,
    @JsonProperty("text") val text: String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Command(
    @JsonProperty("command") val command: String,
    @JsonProperty("description") val description: String
)

package com.ssanchozzz.telebreak.domain

import com.fasterxml.jackson.annotation.JsonProperty

data class MessageResponse(
        @JsonProperty("ok") val ok: Boolean,
        @JsonProperty("result") val result: Message
)

data class UserResponse(
        @JsonProperty("ok") val ok: Boolean,
        @JsonProperty("result") val result: User
)

data class UpdateResponse(
        @JsonProperty("ok") val ok: Boolean,
        @JsonProperty("result") val result: List<Update>
)

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

data class Chat(
        @JsonProperty("id") val id: Int,
        @JsonProperty("type") val type: String
)

data class Update(
        @JsonProperty("update_id") val id: Int,
        @JsonProperty("message") val message: Message?
)

data class Message(
        @JsonProperty("message_id") val id: Int,
        @JsonProperty("from") val user: User,
        @JsonProperty("date") val date: Int,
        @JsonProperty("text") val text: String?,
        @JsonProperty("chat") val chat: Chat?
)

data class Command(
        @JsonProperty("command") val command: String,
        @JsonProperty("description") val description: String
)
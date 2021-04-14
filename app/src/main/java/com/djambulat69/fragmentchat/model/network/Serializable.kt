package com.djambulat69.fragmentchat.model.network

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// StreamsResponse Serializable defined in ui.channels.streams.StreamsResponseSealed

@Entity(tableName = "streams_table", primaryKeys = ["streamId", "isSubscribed"])
@Serializable
data class Stream(
    @SerialName("name") val name: String,
    @SerialName("stream_id") val streamId: Int
) {
    var topics: List<Topic> = emptyList()
    var isSubscribed: Boolean = false
}

@Serializable
data class TopicsResponse(
    @SerialName("topics") val topics: List<Topic>
)

@Serializable
data class Topic(
    @SerialName("max_id") val maxId: Int,
    @SerialName("name") val name: String
) : java.io.Serializable

@Serializable
data class NarrowSearchOperator(
    @SerialName("operator") val operator: String,
    @SerialName("operand") val operand: String
)

@Serializable
data class MessagesResponse(
    @SerialName("messages") val messages: List<Message>,
    @SerialName("found_oldest") val foundOldest: Boolean
)

@Entity(tableName = "messages_table")
@Serializable
data class Message(
    @SerialName("avatar_url") val avatarUrl: String,
    @SerialName("content") val content: String,
    @SerialName("id") @PrimaryKey val id: Int,
    @SerialName("reactions") var reactions: MutableList<Reaction>,
    @SerialName("sender_email") val senderEmail: String,
    @SerialName("sender_full_name") val senderFullName: String,
    @SerialName("sender_id") val senderId: Int,
    @SerialName("timestamp") val timestamp: Int,
    @SerialName("stream_id") val streamId: Int,
    @SerialName("subject") val topicName: String
)

@Serializable
data class Reaction(
    @SerialName("emoji_code") val emojiCode: String,
    @SerialName("emoji_name") val emojiName: String,
    @SerialName("user_id") val userId: Int,
)

@Serializable
data class OwnUser(
    @SerialName("avatar_url") val avatarUrl: String,
    @SerialName("email") val email: String,
    @SerialName("full_name") val fullName: String,
    @SerialName("timezone") val timezone: String,
    @SerialName("user_id") val userId: Int
)

@Serializable
data class AllUsersResponse(
    @SerialName("members") val users: List<User>,
)

@Serializable
data class User(
    @SerialName("avatar_url") val avatarUrl: String,
    @SerialName("email") val email: String,
    @SerialName("full_name") val fullName: String,
    @SerialName("is_bot") val isBot: Boolean,
    @SerialName("timezone") val timezone: String,
    @SerialName("user_id") val userId: Int,
)

@Serializable
data class StreamIdResponse(
    @SerialName("stream_id") val streamId: Int
)

@Serializable
data class UserPresenceResponse(
    @SerialName("presence") val presence: Presence,
)

@Serializable
data class Presence(
    @SerialName("ZulipMobile") val zulipMobile: PresenceClient? = null,
    @SerialName("aggregated") val aggregated: PresenceClient,
    @SerialName("website") val website: PresenceClient
)

@Serializable
data class PresenceClient(
    @SerialName("status") val status: String,
    @SerialName("timestamp") val timestamp: Int
)

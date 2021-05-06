package com.djambulat69.fragmentchat.model.network

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// StreamsResponse Serializables defined in ui.channels.streams.StreamsResponseSealed

@Entity(tableName = "streams_table", primaryKeys = ["streamId", "isSubscribed"])
@Serializable
class Stream(
    @SerialName("name") val name: String,
    @SerialName("stream_id") val streamId: Int
) {
    var topics: List<Topic> = emptyList()
    var isSubscribed: Boolean = false
}

@Serializable
class TopicsResponse(
    @SerialName("topics") val topics: List<Topic>
)

@Serializable
data class Topic(
    @SerialName("max_id") val maxId: Int,
    @SerialName("name") val name: String
)

@Serializable
class NarrowSearchOperator(
    @SerialName("operator") val operator: String,
    @SerialName("operand") val operand: String
) {
    companion object {
        const val STREAM_OPERATOR = "stream"
        const val TOPIC_OPERATOR = "topic"
    }
}

@Serializable
class MessagesResponse(
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
) : java.io.Serializable

@Serializable
data class Reaction(
    @SerialName("emoji_code") val emojiCode: String,
    @SerialName("emoji_name") val emojiName: String,
    @SerialName("user_id") val userId: Int,
)

@Serializable
class AllUsersResponse(
    @SerialName("members") val users: List<User>,
)

@Serializable
class User(
    @SerialName("avatar_url") val avatarUrl: String,
    @SerialName("email") val email: String,
    @SerialName("full_name") val fullName: String,
    @SerialName("is_bot") val isBot: Boolean,
    @SerialName("timezone") val timezone: String,
    @SerialName("user_id") val userId: Int,
)

@Serializable
class UserPresenceResponse(
    @SerialName("presence") val presence: Presence,
)

@Serializable
class Presence(
    @SerialName("ZulipMobile") val zulipMobile: PresenceClient? = null,
    @SerialName("aggregated") val aggregated: PresenceClient,
    @SerialName("website") val website: PresenceClient
)

@Serializable
class PresenceClient(
    @SerialName("status") val status: String,
    @SerialName("timestamp") val timestamp: Int
)

@Serializable
data class RegisterEventResponse(
    @SerialName("queue_id") val queueId: String,
    @SerialName("last_event_id") val lastEventId: Int
)

@Serializable
data class GetEventsResponse(
    @SerialName("result") val result: String,
    @SerialName("msg") val msg: String,
    @SerialName("events") val events: List<Event>,
    @SerialName("queue_id") val queueId: String? = null
)

@Serializable
data class Event(
    @SerialName("type") val type: String,
    @SerialName("message") val message: Message? = null,
    @SerialName("flags") val flags: List<String>? = null,
    @SerialName("id") val id: Int
)

@Serializable
class Subscribtion(
    @SerialName("name") val name: String,
    @SerialName("description") val description: String
)

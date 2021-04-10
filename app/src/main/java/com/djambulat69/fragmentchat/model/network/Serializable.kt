package com.djambulat69.fragmentchat.model.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// StreamsResponse Serializable defined in ui.channels.streams.StreamsResponseSealed

@Serializable
data class Stream(
    @SerialName("description")
    val description: String,
    @SerialName("invite_only")
    val inviteOnly: Boolean,
    @SerialName("name")
    val name: String,
    @SerialName("stream_id")
    val streamId: Int
) {
    var topics: List<Topic> = emptyList()
}

@Serializable
data class TopicsResponse(
    @SerialName("msg")
    val msg: String,
    @SerialName("result")
    val result: String,
    @SerialName("topics")
    val topics: List<Topic>
)

@Serializable
data class Topic(
    @SerialName("max_id")
    val maxId: Int,
    @SerialName("name")
    val name: String
) : java.io.Serializable

@Serializable
data class NarrowSearchOperator(
    @SerialName("operator")
    val operator: String,
    @SerialName("operand")
    val operand: String
)

@Serializable
data class MessagesResponse(
    @SerialName("found_anchor")
    val foundAnchor: Boolean,
    @SerialName("found_newest")
    val foundNewest: Boolean,
    @SerialName("messages")
    val messages: List<Message>,
    @SerialName("msg")
    val msg: String,
    @SerialName("result")
    val result: String
)

@Serializable
data class Message(
    @SerialName("avatar_url")
    val avatarUrl: String,
    @SerialName("client")
    val client: String,
    @SerialName("content")
    val content: String,
    @SerialName("content_type")
    val contentType: String,
    @SerialName("flags")
    val flags: List<String>,
    @SerialName("id")
    val id: Int,
    @SerialName("is_me_message")
    val isMeMessage: Boolean,
    @SerialName("reactions")
    var reactions: MutableList<Reaction>,
    @SerialName("recipient_id")
    val recipientId: Int,
    @SerialName("sender_email")
    val senderEmail: String,
    @SerialName("sender_full_name")
    val senderFullName: String,
    @SerialName("sender_id")
    val senderId: Int,
    @SerialName("sender_realm_str")
    val senderRealmStr: String,
    @SerialName("subject")
    val subject: String,
    @SerialName("submessages")
    val submessages: List<String>,
    @SerialName("timestamp")
    val timestamp: Int,
    @SerialName("topic_links")
    val topicLinks: List<String>,
    @SerialName("type")
    val type: String,
    @SerialName("stream_id")
    val streamId: Int? = null
)

@Serializable
data class Reaction(
    @SerialName("emoji_code")
    val emojiCode: String,
    @SerialName("emoji_name")
    val emojiName: String,
    @SerialName("reaction_type")
    val reactionType: String, // one of: "unicode_emoji", "realm_emoji", "zulip_extra_emoji"
    @SerialName("user_id")
    val userId: Int,
)

@Serializable
data class OwnUser(
    @SerialName("avatar_url")
    val avatarUrl: String,
    @SerialName("avatar_version")
    val avatarVersion: Int,
    @SerialName("date_joined")
    val dateJoined: String,
    @SerialName("email")
    val email: String,
    @SerialName("full_name")
    val fullName: String,
    @SerialName("is_active")
    val isActive: Boolean,
    @SerialName("is_admin")
    val isAdmin: Boolean,
    @SerialName("is_bot")
    val isBot: Boolean,
    @SerialName("is_guest")
    val isGuest: Boolean,
    @SerialName("is_owner")
    val isOwner: Boolean,
    @SerialName("max_message_id")
    val maxMessageId: Int,
    @SerialName("msg")
    val msg: String,
    @SerialName("result")
    val result: String,
    @SerialName("timezone")
    val timezone: String,
    @SerialName("user_id")
    val userId: Int
)

@Serializable
data class AllUsersResponse(
    @SerialName("members")
    val users: List<User>,
    @SerialName("msg")
    val msg: String,
    @SerialName("result")
    val result: String
)

@Serializable
data class User(
    @SerialName("avatar_url")
    val avatarUrl: String,
    @SerialName("date_joined")
    val dateJoined: String,
    @SerialName("email")
    val email: String,
    @SerialName("full_name")
    val fullName: String,
    @SerialName("is_active")
    val isActive: Boolean,
    @SerialName("is_admin")
    val isAdmin: Boolean,
    @SerialName("is_bot")
    val isBot: Boolean,
    @SerialName("is_guest")
    val isGuest: Boolean,
    @SerialName("is_owner")
    val isOwner: Boolean,
    @SerialName("timezone")
    val timezone: String,
    @SerialName("user_id")
    val userId: Int,
)

@Serializable
data class SendMessageResponse(
    @SerialName("id")
    val id: Int,
    @SerialName("msg")
    val msg: String,
    @SerialName("result")
    val result: String
)

@Serializable
data class StreamIdResponse(
    @SerialName("msg")
    val msg: String,
    @SerialName("result")
    val result: String,
    @SerialName("stream_id")
    val streamId: Int
)

@Serializable
data class UserPresenceResponse(
    @SerialName("msg")
    val msg: String,
    @SerialName("presence")
    val presence: Presence,
    @SerialName("result")
    val result: String
)

@Serializable
data class Presence(
    @SerialName("ZulipMobile")
    val zulipMobile: PresenceClient? = null,
    @SerialName("aggregated")
    val aggregated: PresenceClient,
    @SerialName("website")
    val website: PresenceClient
)

@Serializable
data class PresenceClient(
    @SerialName("status")
    val status: String,
    @SerialName("timestamp")
    val timestamp: Int
)

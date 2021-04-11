package com.djambulat69.fragmentchat.model.db

import androidx.room.TypeConverter
import com.djambulat69.fragmentchat.model.network.Reaction
import com.djambulat69.fragmentchat.model.network.Topic
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TypeConverters {
    @TypeConverter
    fun topicsToJsonString(topics: List<Topic>?): String? {
        return topics?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun jsonStringToTopics(topicsJsonString: String?): List<Topic>? {
        return topicsJsonString?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun reactionsToJsonString(reactions: List<Reaction>?): String? {
        return reactions?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun jsonStringToReactions(reactionsJsonString: String?): List<Reaction>? {
        return reactionsJsonString?.let { Json.decodeFromString(it) }
    }
}

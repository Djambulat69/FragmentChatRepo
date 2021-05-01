package com.djambulat69.fragmentchat.model.db.converters

import com.djambulat69.fragmentchat.model.db.TypeConverter
import com.djambulat69.fragmentchat.model.network.Topic
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TopicsConverter : TypeConverter<List<Topic>, String>() {

    @androidx.room.TypeConverter
    override fun convert(value: List<Topic>): String {
        return Json.encodeToString(value)
    }

    @androidx.room.TypeConverter
    override fun retrieve(value: String): List<Topic> {
        return Json.decodeFromString(value)
    }
}

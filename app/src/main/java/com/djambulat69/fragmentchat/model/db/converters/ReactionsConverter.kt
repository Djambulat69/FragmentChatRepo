package com.djambulat69.fragmentchat.model.db.converters

import com.djambulat69.fragmentchat.model.db.TypeConverter
import com.djambulat69.fragmentchat.model.network.Reaction
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ReactionsConverter : TypeConverter<List<Reaction>, String>() {

    @androidx.room.TypeConverter
    override fun convert(value: List<Reaction>): String {
        return Json.encodeToString(value)
    }

    @androidx.room.TypeConverter
    override fun retrieve(value: String): List<Reaction> {
        return Json.decodeFromString(value)
    }

}

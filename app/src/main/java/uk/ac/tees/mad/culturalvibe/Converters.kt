package uk.ac.tees.mad.culturalvibe

import androidx.room.TypeConverter
import com.google.firebase.Timestamp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromTimestamp(timestamp: Timestamp?): Long? = timestamp?.seconds

    @TypeConverter
    fun toTimestamp(seconds: Long?): Timestamp? =
        seconds?.let { Timestamp(it, 0) }

    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toStringList(data: String?): List<String> {
        if (data.isNullOrEmpty()) return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(data, type)
    }
}
package uk.ac.tees.mad.culturalvibe.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp

@Entity(tableName = "bookmarked_events")
data class Event(
    @PrimaryKey val id: Int = 0,
    val title: String = "",
    val date: Timestamp? = null,
    val description: String = "",
    val venue: String = "",
    val fee: Double = 0.0,
    val imageUrl: String = "",
    var registeredUser: List<String> = emptyList()
)
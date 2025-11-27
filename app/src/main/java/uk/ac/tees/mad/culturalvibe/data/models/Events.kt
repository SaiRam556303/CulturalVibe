package uk.ac.tees.mad.culturalvibe.data.models

import com.google.firebase.Timestamp

data class Event(
    val id: Int = 0,
    val title: String = "",
    val date: Timestamp? = null,
    val description: String = "",
    val venue: String = "",
    val fee: Double = 0.0,
    val imageUrl: String = "",
    var registeredUser: List<String> = emptyList()
)
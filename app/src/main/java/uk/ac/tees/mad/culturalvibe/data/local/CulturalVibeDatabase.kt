package uk.ac.tees.mad.culturalvibe.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import uk.ac.tees.mad.culturalvibe.Converters
import uk.ac.tees.mad.culturalvibe.data.models.Event

@Database(entities = [Event::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class CulturalVibeDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
}

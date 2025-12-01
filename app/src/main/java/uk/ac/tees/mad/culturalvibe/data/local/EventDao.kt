package uk.ac.tees.mad.culturalvibe.data.local
import androidx.room.*
import uk.ac.tees.mad.culturalvibe.data.models.Event

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(event: Event)

    @Delete
    suspend fun deleteBookmark(event: Event)

    @Query("SELECT * FROM bookmarked_events")
    suspend fun getAllBookmarks(): List<Event>
}

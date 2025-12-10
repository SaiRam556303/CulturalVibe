package uk.ac.tees.mad.culturalvibe.data.repository

import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uk.ac.tees.mad.culturalvibe.data.remote.SupabaseClientProvider
import javax.inject.Inject

class GalleryRepository @Inject constructor(){

    val storage = SupabaseClientProvider.client.storage

    suspend fun getGalleryImages(): List<String> = withContext(Dispatchers.IO) {
        val bucket = storage.from("CulturalVibe")
        val files = bucket.list()
        files.map { file ->
            bucket.publicUrl(file.name)
        }
    }
}

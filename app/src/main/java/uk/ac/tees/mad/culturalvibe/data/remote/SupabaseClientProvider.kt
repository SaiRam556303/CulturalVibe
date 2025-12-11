package uk.ac.tees.mad.culturalvibe.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClientProvider {
    private const val SUPABASE_URL = "https://ejdtjvdhcrgivjmskaop.supabase.co"
    private const val SUPABASE_KEY = "sb_publishable_ov6ea3euVHEOZ1kf8I2UjA_rXufw6yK"

    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Storage)
        install(Postgrest)
    }
}
package uk.ac.tees.mad.culturalvibe.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClientProvider {
    private const val SUPABASE_URL = "https://hkomnxxbxxvnopisqbjc.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imhrb21ueHhieHh2bm9waXNxYmpjIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTczMjI5MTksImV4cCI6MjA3Mjg5ODkxOX0.CKY2_O7VlgT2vCmDqssd9tTwrLl5PRlp8dys3cFwf8s"

    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Storage)
        install(Postgrest)
    }
}
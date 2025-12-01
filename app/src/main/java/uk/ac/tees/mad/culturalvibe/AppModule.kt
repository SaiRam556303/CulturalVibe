package uk.ac.tees.mad.culturalvibe

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uk.ac.tees.mad.culturalvibe.data.local.CulturalVibeDatabase
import uk.ac.tees.mad.culturalvibe.data.local.EventDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun providesFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideDatabase(context: Context): CulturalVibeDatabase {
        return Room.databaseBuilder(
            context,
            CulturalVibeDatabase::class.java,
            "CulturalVibeDatabase"
        ).build()
    }

    @Provides
    fun provideEventDao(db: CulturalVibeDatabase): EventDao = db.eventDao()

}
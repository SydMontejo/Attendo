package com.example.attendo.DAO

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object module {
    @Provides
    @Singleton
    fun provideDatabase(app: Context): AppDatabase {
        return Room.databaseBuilder(app, AppDatabase::class.java, "event_manager_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao {
        return db.userDao()
    }

    @Provides
    fun provideEventDao(db: AppDatabase): EventDao {
        return db.eventDao()
    }
}
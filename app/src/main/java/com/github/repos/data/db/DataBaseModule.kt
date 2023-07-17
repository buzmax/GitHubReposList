package com.github.repos.data.db

import android.content.Context
import androidx.room.Room
import com.github.repos.data.network.GitHubService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "repository_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideRepositoryDao(database: AppDatabase): RepositoryDao {
        return database.repositoryDao()
    }
}
package com.github.repos.data.repo

import android.content.Context
import androidx.preference.PreferenceManager
import com.github.repos.data.db.RepositoryDao
import com.github.repos.data.network.GitHubService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideGitHubRepository(
        service: GitHubService,
        repositoryDao: RepositoryDao,
        @ApplicationContext context: Context
    ): GitHubRepository {
        return GitHubRepository(
            service,
            repositoryDao,
            PreferenceManager.getDefaultSharedPreferences(context)
        )
    }
}
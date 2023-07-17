package com.github.repos.di

import com.github.repos.data.db.DataBaseModule
import com.github.repos.data.network.NetworkModule
import com.github.repos.data.repo.RepositoryModule
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module(includes = [
    NetworkModule::class,
    DataBaseModule::class,
    RepositoryModule::class,
])
@InstallIn(SingletonComponent::class)
object AppModule {

}
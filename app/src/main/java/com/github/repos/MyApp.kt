package com.github.repos

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GitHubReposApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
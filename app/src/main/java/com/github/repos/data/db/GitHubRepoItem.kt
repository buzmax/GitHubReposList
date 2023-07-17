package com.github.repos.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repositories")
data class GitHubRepoItem(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String,
    val owner: String,
    val avatarUrl: String?,
    val stargazersCount: Int,
    val repositoryUrl: String,
    val timestamp: Long = System.currentTimeMillis()
)
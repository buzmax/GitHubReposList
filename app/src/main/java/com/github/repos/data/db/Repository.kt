package com.github.repos.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repositories")
data class Repository(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String,
    val owner: String,
    val timestamp: Long = System.currentTimeMillis()
)
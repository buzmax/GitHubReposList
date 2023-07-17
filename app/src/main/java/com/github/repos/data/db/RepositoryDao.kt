package com.github.repos.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RepositoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repositories: List<Repository>)

    @Query("SELECT * FROM repositories ORDER BY timestamp")
    fun getRepositories(): PagingSource<Int, Repository>

    @Query("DELETE FROM repositories")
    suspend fun deleteAllRepositories()



//    @Query("SELECT MAX(page) FROM repositories WHERE id = :repositoryId")
//    suspend fun getPageForRepository(repositoryId: Long): Int?
}
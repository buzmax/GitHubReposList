package com.github.repos.data.repo

import android.content.SharedPreferences
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.github.repos.Constants
import com.github.repos.data.db.GitHubRepoItem
import com.github.repos.data.db.RepositoryDao
import com.github.repos.data.network.GitHubApi
import kotlinx.coroutines.flow.Flow


class GitHubRepository constructor(
    private val service: GitHubApi,
    private val repositoryDao: RepositoryDao,
    private val sharedPrefs: SharedPreferences
) {
    @OptIn(ExperimentalPagingApi::class)
    fun getRepositories(
        pageSize: Int = Constants.PAGE_SIZE
    ): Flow<PagingData<GitHubRepoItem>> {
        return Pager(
            config = PagingConfig(pageSize = pageSize),
            remoteMediator = GitHubRemoteMediator(service, repositoryDao, sharedPrefs)
        ) {
            repositoryDao.getRepositories()
        }.flow
    }
}

package com.github.repos.data.repo

import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.github.repos.Constants
import com.github.repos.data.db.GitHubRepoItem
import com.github.repos.data.db.RepositoryDao
import com.github.repos.data.network.GitHubApi
import java.io.IOException
import java.util.Locale


// TODO: create domain layer with usecases, domain models, introduce data source layer,
//  and move mapping to domain models to that layer
@OptIn(ExperimentalPagingApi::class)
class GitHubRemoteMediator(
    private val service: GitHubApi,
    private val repositoryDao: RepositoryDao,
    private val sharedPrefs: SharedPreferences
) : RemoteMediator<Int, GitHubRepoItem>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, GitHubRepoItem>
    ): MediatorResult {
        try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    sharedPrefs.getInt(LAST_PAGE_INDEX_KEY, -1)
                        .let { index ->
                            if (index > 0) {
                                index.plus(1)
                            } else {
                                return MediatorResult.Success(endOfPaginationReached = true)
                            }
                        }
                }
            }

            val date = getThirtyDaysAgoDateString()

            val response = service.getRepositories(
                query = "created:>=$date",
                page = page,
                perPage = state.config.pageSize
            )

            if (response.isSuccessful) {
                val repositories = response.body()
                    ?.items
                    // todo: extract to mapper and cover with unit tests
                    ?.map { item ->
                        GitHubRepoItem(
                            id = item.id,
                            name = item.name,
                            description = item.description ?: "",
                            owner = item.owner.login,
                            avatarUrl = item.owner.avatarUrl,
                            stargazersCount = item.stargazersCount,
                            repositoryUrl = item.svnUrl
                        )
                    } ?: emptyList()


                if (loadType == LoadType.REFRESH) {
                    repositoryDao.deleteAllRepositories()
                }

                repositoryDao.insertAll(repositories)
                sharedPrefs.edit().putInt(LAST_PAGE_INDEX_KEY, page).apply()

                val endOfPaginationReached = repositories.isEmpty()
                return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
            }

            return MediatorResult.Error(
                IOException(
                    "Error fetching repositories: ${
                        response.errorBody()?.string()
                    }"
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return MediatorResult.Error(e)
        }
    }

    private fun getThirtyDaysAgoDateString(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -30)
        val thirtyDaysAgo = calendar.time
        val dateFormat = SimpleDateFormat(Constants.DATE_FORMAT, Locale.US)
        return dateFormat.format(thirtyDaysAgo)
    }

    companion object {
        val TAG = GitHubRemoteMediator::class.simpleName
        const val LAST_PAGE_INDEX_KEY = "LAST_PAGE_INDEX"
    }
}
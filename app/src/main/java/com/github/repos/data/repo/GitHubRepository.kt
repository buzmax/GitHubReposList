package com.github.repos.data.repo

import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.github.repos.data.db.DateTypeConverter
import com.github.repos.data.db.Repository
import com.github.repos.data.db.RepositoryDao
import com.github.repos.data.network.GitHubService
import kotlinx.coroutines.flow.Flow
import java.io.IOException
import java.util.Locale

class GitHubRepository constructor(
    private val service: GitHubService,
    private val repositoryDao: RepositoryDao,
    private val sharedPrefs: SharedPreferences
) {
    @OptIn(ExperimentalPagingApi::class)
    fun getRepositories(pageSize: Int = 20): Flow<PagingData<Repository>> {
//        return Pager(
//            config = PagingConfig(pageSize = pageSize),
//            pagingSourceFactory = { com.github.repos.data.repo.RepositoryPagingSource(service, repositoryDao) }
//        ).flow
        return Pager(
            config = PagingConfig(pageSize = pageSize),
            remoteMediator = GitHubRemoteMediator(service, repositoryDao, sharedPrefs)
        ) {
            repositoryDao.getRepositories()
        }.flow
    }
}

@OptIn(ExperimentalPagingApi::class)
class GitHubRemoteMediator(
    private val service: GitHubService,
    private val repositoryDao: RepositoryDao,
    private val sharedPrefs: SharedPreferences
) : RemoteMediator<Int, Repository>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Repository>
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

            Log.d(TAG, "new request: page = $page, date = $date")

            val response = service.getRepositories(
                query = "created:>=$date",
                page = page,
                perPage = state.config.pageSize
            )

            if (response.isSuccessful) {
                val repositories = response.body()
                    ?.items
                    // todo: extract to mapper layer and cover with unit tests
                    ?.map { item ->
                        Log.d(TAG, "created at = ${item.created_at}")
                        Repository(
                            id = item.id,
                            name = item.name,
                            description = item.description ?: "",
                            owner = item.owner.login
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

            return MediatorResult.Error(IOException("Error fetching repositories: ${response.errorBody()?.string()}"))
        } catch (e: Exception) {
            e.printStackTrace()
            return MediatorResult.Error(e)
        }
    }

    private fun getThirtyDaysAgoDateString(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -30)
        val thirtyDaysAgo = calendar.time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return dateFormat.format(thirtyDaysAgo)
    }

    companion object {
        val TAG = GitHubRemoteMediator::class.simpleName
        const val LAST_PAGE_INDEX_KEY = "LAST_PAGE_INDEX"
    }
}


//class RepositoryPagingSource(
//    private val service: GitHubService,
//    private val repositoryDao: RepositoryDao
//) : PagingSource<Int, Repository>() {
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Repository> {
//        return try {
//            val page: Int = params.key ?: 1
//            val date: String = getThirtyDaysAgoDateString()
//            val response = service.getRepositories(
//                query = "created:>=$date",
//                page = page,
//                perPage = params.loadSize
//            )
//
//            if (response.isSuccessful) {
//                val repositories = response.body()?.repositories ?: emptyList()
//                repositoryDao.insertAll(repositories)
//                LoadResult.Page(
//                    data = repositories,
//                    prevKey = if (page == 1) null else page - 1,
//                    nextKey = if (repositories.isNotEmpty()) page + 1 else null
//                )
//            } else {
//                LoadResult.Error(IOException("Error fetching repositories: ${response.message()}"))
//            }
//        } catch (e: Exception) {
//            LoadResult.Error(e)
//        }
//    }
//
//    override fun getRefreshKey(state: PagingState<Int, Repository>): Int? {
//        return state.anchorPosition?.let { anchorPosition ->
//            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
//                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
//        }
//    }
//
//    private fun getThirtyDaysAgoDateString(): String {
//        val calendar = Calendar.getInstance()
//        calendar.add(Calendar.DAY_OF_YEAR, -30)
//        val thirtyDaysAgo = calendar.time
//        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
//        return dateFormat.format(thirtyDaysAgo)
//    }
//}
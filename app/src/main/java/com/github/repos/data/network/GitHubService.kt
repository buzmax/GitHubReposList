package com.github.repos.data.network

import com.github.repos.data.network.response.GitHubRepoSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GitHubService {
    @GET("search/repositories")
    suspend fun getRepositories(
        @Query("q") query: String,
        @Query("sort") sort: String = "created_at",
//        @Query("order") order: String = "asc",
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Response<GitHubRepoSearchResponse>
}


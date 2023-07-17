package com.github.repos.ui

import com.github.repos.data.repo.GitHubRepository
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.github.repos.data.db.GitHubRepoItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class GitHubReposViewModel @Inject constructor(
    repository: GitHubRepository
) : ViewModel() {

    val repositories: Flow<PagingData<GitHubRepoItem>> = repository.getRepositories()
}
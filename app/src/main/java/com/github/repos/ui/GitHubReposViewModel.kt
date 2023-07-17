package com.github.repos.ui

import com.github.repos.data.repo.GitHubRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.github.repos.data.db.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GitHubReposViewModel @Inject constructor(
    private val repository: GitHubRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.getRepositories()
        }
    }

    val repositories: Flow<PagingData<Repository>> = repository.getRepositories()
}
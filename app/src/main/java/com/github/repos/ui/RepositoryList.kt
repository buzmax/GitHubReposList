package com.github.repos.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.github.repos.R
import com.github.repos.data.db.GitHubRepoItem

@Composable
fun ReposList(
    viewModel: GitHubReposViewModel,
    onItemClick: (GitHubRepoItem) -> Unit
) {
    val repositories: LazyPagingItems<GitHubRepoItem> =
        viewModel.repositories.collectAsLazyPagingItems()
    val lazyListState = rememberLazyListState()

    LazyColumn(
        state = lazyListState,
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            repositories.itemCount
        ) {
            repositories[it]?.also { item ->
                RepoItem(
                    gitHubRepoItem = item,
                    onItemClick = onItemClick
                )
            }
        }

        repositories.apply {
            when {
                loadState.refresh is LoadState.Loading -> item {
                    Box(Modifier.fillMaxWidth()) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.Center)
                        )
                    }
                }

                loadState.refresh is LoadState.Error -> item {
                    Text(
                        text = "Error loading repositories",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        color = Color.Red
                    )
                }

                loadState.append is LoadState.Loading -> item {
                    Box(Modifier.fillMaxWidth()) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.Center)
                        )
                    }
                }

                loadState.append is LoadState.Error -> item {
                    Text(
                        text = (loadState.append as LoadState.Error).error.message
                            ?: "Error loading more repos",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        color = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
fun RepoItem(gitHubRepoItem: GitHubRepoItem, onItemClick: (GitHubRepoItem) -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onItemClick(gitHubRepoItem) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = gitHubRepoItem.name, style = MaterialTheme.typography.h6)
            Text(text = gitHubRepoItem.description, style = MaterialTheme.typography.body1)
            AsyncImage(
                modifier = Modifier.size(36.dp),
                model = gitHubRepoItem.avatarUrl,
                contentDescription = gitHubRepoItem.description,
                error = painterResource(R.drawable.avatar_placeholder)
            )
            Text(text = gitHubRepoItem.owner, style = MaterialTheme.typography.caption)
        }
    }
}
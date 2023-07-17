package com.github.repos.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.github.repos.data.db.Repository

@Composable
fun ReposList(
    viewModel: GitHubReposViewModel,
    onItemClick: (Repository) -> Unit
) {
    val repositories: LazyPagingItems<Repository> =
        viewModel.repositories.collectAsLazyPagingItems()

    val lazyListState = rememberLazyListState()
    LazyColumn(
        state = lazyListState,
        modifier = Modifier.fillMaxWidth()
    ) {
        items(
            repositories.itemCount
        ) {
            RepoItem(repository = repositories[it]!!, onItemClick = onItemClick)
        }

        repositories.apply {
            when {
                loadState.refresh is LoadState.Loading -> item {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
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
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
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
fun RepoItem(repository: Repository, onItemClick: (Repository) -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onItemClick(repository) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = repository.name, style = MaterialTheme.typography.h6)
            Text(text = repository.description, style = MaterialTheme.typography.body1)
            Text(text = repository.owner, style = MaterialTheme.typography.caption)
        }
    }
}
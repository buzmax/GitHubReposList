package com.github.repos.ui

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class GitHubReposActivity : ComponentActivity() {
    private val viewModel: GitHubReposViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GitHubReposTheme {
                Surface(color = MaterialTheme.colors.background) {
                    ReposList(
                        viewModel = viewModel,
                    ) { repository ->
                        val intent = CustomTabsIntent.Builder()
                            .build()
                        intent.launchUrl(this, Uri.parse(repository.repositoryUrl))
                    }
                }
            }
        }
    }
}
package com.github.repos.data.network.response

data class Item(
    val owner: Owner,
    val name: String,
    val description: String?,
    val stargazersCount: Int,
    val svnUrl: String,
)
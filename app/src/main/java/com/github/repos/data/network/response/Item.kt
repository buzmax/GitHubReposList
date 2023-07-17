package com.github.repos.data.network.response

import com.google.gson.annotations.SerializedName

data class Item(
    val id: Int,
    val owner: Owner,
    val name: String,
    val description: String?,
    @SerializedName("stargazers_count")
    val stargazersCount: Int,
    @SerializedName("svn_url")
    val svnUrl: String,
)
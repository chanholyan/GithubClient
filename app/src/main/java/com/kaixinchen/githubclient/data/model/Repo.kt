package com.kaixinchen.githubclient.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RepoSearchResponse(
    @Json(name = "total_count") val totalCount: Int,
    val items: List<Repo>
)

@JsonClass(generateAdapter = true)
data class Repo(
    val id: Long,
    val name: String,
    @Json(name = "full_name") val fullName: String,
    val description: String?,
    @Json(name = "stargazers_count") val stargazersCount: Int,
    val language: String?,
    @Json(name = "html_url") val htmlUrl: String
)

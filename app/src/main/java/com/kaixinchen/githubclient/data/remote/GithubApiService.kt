package com.kaixinchen.githubclient.data.remote

import com.kaixinchen.githubclient.data.model.RepoSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubApiService {

    @GET("search/repositories")
    suspend fun searchRepos(
        @Query("q") query: String,
        @Query("sort") sort: String = "stars"
    ): RepoSearchResponse

    @GET("user/repos")
    suspend fun getMyRepos()
}

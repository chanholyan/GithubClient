package com.kaixinchen.githubclient.data.remote

import com.kaixinchen.githubclient.data.model.Repo
import com.kaixinchen.githubclient.data.model.RepoSearchResponse
import com.squareup.moshi.JsonClass
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApiService {

    @GET("search/repositories")
    suspend fun searchRepos(
        @Query("q") query: String,
        @Query("sort") sort: String = "stars"
    ): RepoSearchResponse

    @GET("user/repos")
    suspend fun getMyRepos(
        @Query("sort") sort: String = "updated",
        @Query("visibility") visibility: String = "all"
    ): List<Repo>

    @POST("repos/{owner}/{repo}/issues")
    suspend fun createIssue(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Body issueBody: IssueRequest
    )
}

@JsonClass(generateAdapter = true)
data class IssueRequest(
    val title: String,
    val body: String
)

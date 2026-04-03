package com.kaixinchen.githubclient.data.repository

import com.kaixinchen.githubclient.data.model.Repo
import com.kaixinchen.githubclient.data.remote.GithubApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GithubRepositoryImpl @Inject constructor(
    private val apiService: GithubApiService
) : GithubRepository {

    override suspend fun searchRepositories(query: String): Result<List<Repo>> {
        return try {
            val response = apiService.searchRepos(query = query)
            Result.success(response.items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

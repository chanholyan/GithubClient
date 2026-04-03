package com.kaixinchen.githubclient.data.repository

import com.kaixinchen.githubclient.data.model.Repo
import com.kaixinchen.githubclient.data.remote.GithubApiService
import com.kaixinchen.githubclient.data.remote.IssueRequest
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

    override suspend fun getMyRepositories(): Result<List<Repo>> {
        return try {
            val response = apiService.getMyRepos()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createIssue(owner: String, repo: String, title: String, body: String): Result<Unit> {
        return try {
            apiService.createIssue(owner, repo, IssueRequest(title, body))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

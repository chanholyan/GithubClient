package com.kaixinchen.githubclient.data.repository

import com.kaixinchen.githubclient.data.model.Repo

interface GithubRepository {
    suspend fun searchRepositories(query: String): Result<List<Repo>>
    suspend fun getMyRepositories(): Result<List<Repo>>
    suspend fun createIssue(owner: String, repo: String, title: String, body: String): Result<Unit> // 🌟 New
}

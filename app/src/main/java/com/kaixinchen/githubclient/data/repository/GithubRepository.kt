package com.kaixinchen.githubclient.data.repository

import com.kaixinchen.githubclient.data.model.Repo

interface GithubRepository {
    suspend fun searchRepositories(query: String): Result<List<Repo>>
}

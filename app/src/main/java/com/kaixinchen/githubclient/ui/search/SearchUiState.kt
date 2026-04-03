package com.kaixinchen.githubclient.ui.search

import com.kaixinchen.githubclient.data.model.Repo

sealed interface SearchUiState {
    object Idle : SearchUiState
    object Loading : SearchUiState
    data class Success(val repos: List<Repo>) : SearchUiState
    data class Error(val message: String) : SearchUiState
}

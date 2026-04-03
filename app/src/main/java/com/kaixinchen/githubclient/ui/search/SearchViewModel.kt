package com.kaixinchen.githubclient.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaixinchen.githubclient.data.local.AuthManager
import com.kaixinchen.githubclient.data.repository.GithubRepository
import com.kaixinchen.githubclient.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: GithubRepository,
    private val authManager: AuthManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Loading)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        fetchPopularRepos()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query.trim()
    }

    fun search() {
        val query = _searchQuery.value
        if (query.isBlank() || query.length < Constants.Search.MIN_SEARCH_LENGTH) return
        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading

            val formattedQuery = "language:$query"

            val result = repository.searchRepositories(formattedQuery)

            result.fold(
                onSuccess = { repos ->
                    _uiState.value = SearchUiState.Success(repos)
                },
                onFailure = { error ->
                    _uiState.value = SearchUiState.Error(error.message ?: Constants.Error.DEFAULT_ERROR_MESSAGE)
                }
            )
        }
    }

    fun fetchPopularRepos() {
        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            val result = repository.searchRepositories(Constants.Search.POPULAR_REPOS_QUERY)
            result.fold(
                onSuccess = { repos -> _uiState.value = SearchUiState.Success(repos) },
                onFailure = { error -> _uiState.value = SearchUiState.Error(error.message ?: Constants.Error.DEFAULT_ERROR_MESSAGE) }
            )
        }
    }

    fun retry() {
        _uiState.value = SearchUiState.Loading
        val query = _searchQuery.value
        if (query.isBlank()) {
            fetchPopularRepos()
        } else {
            viewModelScope.launch {
                val formattedQuery = "language:$query"
                val result = repository.searchRepositories(formattedQuery)
                result.fold(
                    onSuccess = { repos -> _uiState.value = SearchUiState.Success(repos) },
                    onFailure = { error -> _uiState.value = SearchUiState.Error(error.message ?: "Unknown Error") }
                )
            }
        }
    }
}

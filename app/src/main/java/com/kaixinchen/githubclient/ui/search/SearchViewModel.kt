package com.kaixinchen.githubclient.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaixinchen.githubclient.data.repository.GithubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: GithubRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun search() {
        val query = _searchQuery.value
        if (query.isBlank()) return
        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading

            // Search for the language, e.g. pass "kotlin" and will format as "language:kotlin"
            val formattedQuery = "language:$query"

            val result = repository.searchRepositories(formattedQuery)

            result.fold(
                onSuccess = { repos ->
                    _uiState.value = SearchUiState.Success(repos)
                },
                onFailure = { error ->
                    _uiState.value = SearchUiState.Error(error.message ?: "Unknown Error occurred")
                }
            )
        }
    }
}

package com.kaixinchen.githubclient.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.kaixinchen.githubclient.data.model.Repo
import com.kaixinchen.githubclient.data.local.AuthManager
import com.kaixinchen.githubclient.data.repository.GithubRepository
import javax.inject.Inject

sealed interface ProfileUiState {
    object NotLoggedIn : ProfileUiState
    object Loading : ProfileUiState
    data class Success(val repos: List<Repo>) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: GithubRepository,
    private val authManager: AuthManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        // Check login status first, if not logged in, set to NotLoggedIn state
        if (!authManager.isLoggedIn()) {
            _uiState.value = ProfileUiState.NotLoggedIn
        } else {
            fetchMyRepos()
        }
    }

    fun fetchMyRepos() {
        if (!authManager.isLoggedIn()) {
            _uiState.value = ProfileUiState.NotLoggedIn
            return
        }

        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            val result = repository.getMyRepositories()
            result.fold(
                onSuccess = { repos -> _uiState.value = ProfileUiState.Success(repos) },
                onFailure = { error -> _uiState.value = ProfileUiState.Error(error.message ?: "Failed to load repos") }
            )
        }
    }
}

package com.kaixinchen.githubclient.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaixinchen.githubclient.data.local.AuthManager
import com.kaixinchen.githubclient.data.repository.GithubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface IssueSubmitState {
    object Idle : IssueSubmitState
    object Loading : IssueSubmitState
    object Success : IssueSubmitState
    data class Error(val message: String) : IssueSubmitState
}

@HiltViewModel
class RepoDetailViewModel @Inject constructor(
    private val repository: GithubRepository,
    authManager: AuthManager
) : ViewModel() {

    val isLoggedIn = authManager.isLoggedIn()

    private val _submitState = MutableStateFlow<IssueSubmitState>(IssueSubmitState.Idle)
    val submitState = _submitState.asStateFlow()

    fun submitIssue(url: String, title: String, body: String) {
        if (title.isBlank()) {
            _submitState.value = IssueSubmitState.Error("Title cannot be empty")
            return
        }

        val parts = url.substringAfter("github.com/").split("/")
        val owner = parts.getOrNull(0)
        val repo = parts.getOrNull(1)

        if (owner == null || repo == null) {
            _submitState.value = IssueSubmitState.Error("Invalid repository URL")
            return
        }

        viewModelScope.launch {
            _submitState.value = IssueSubmitState.Loading
            
            val result = repository.createIssue(owner, repo, title, body)
            
            result.fold(
                onSuccess = { _submitState.value = IssueSubmitState.Success },
                onFailure = { error ->
                    _submitState.value = IssueSubmitState.Error(error.message ?: "Failed to submit issue")
                }
            )
        }
    }

    fun resetState() {
        _submitState.value = IssueSubmitState.Idle
    }
}
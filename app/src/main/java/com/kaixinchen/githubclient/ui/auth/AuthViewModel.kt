package com.kaixinchen.githubclient.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.kaixinchen.githubclient.data.local.AuthManager
import com.kaixinchen.githubclient.data.repository.GithubRepository
import javax.inject.Inject

sealed interface LoginState {
    object Idle : LoginState
    object Loading : LoginState
    data class Error(val message: String) : LoginState
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val repository: GithubRepository
) : ViewModel() {

    private val _tokenInput = MutableStateFlow("")
    val tokenInput = _tokenInput.asStateFlow()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState = _loginState.asStateFlow()

    fun onTokenChanged(newToken: String) {
        _tokenInput.value = newToken
        if (_loginState.value is LoginState.Error) {
            _loginState.value = LoginState.Idle
        }
    }

    fun login(onSuccess: () -> Unit) {
        val token = _tokenInput.value
        if (token.isBlank()) return

        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            
            authManager.saveToken(token)

            val result = repository.getMyRepositories()

            result.fold(
                onSuccess = {
                    _loginState.value = LoginState.Idle
                    onSuccess()
                },
                onFailure = {
                    authManager.clearToken()
                    _loginState.value = LoginState.Error("Invalid Token! Please check and try again.")
                }
            )
        }
    }
}

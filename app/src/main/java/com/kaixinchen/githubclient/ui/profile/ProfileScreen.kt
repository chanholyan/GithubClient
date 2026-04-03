package com.kaixinchen.githubclient.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kaixinchen.githubclient.ui.search.RepoItem

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    isLoggedIn: Boolean,
    onRepoClick: (String) -> Unit,
    onLoginClick: () -> Unit
) {
    LaunchedEffect(isLoggedIn) {
        viewModel.fetchMyRepos()
    }

    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "My Repositories",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Show repository list or login prompt based on state
        when (val state = uiState) {
            is ProfileUiState.NotLoggedIn -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Please log in to view your repositories.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onLoginClick) {
                        Text("Sign In with PAT")
                    }
                }
            }
            is ProfileUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ProfileUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
            }
            is ProfileUiState.Success -> {
                if (state.repos.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("You don't have any repositories yet.")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.repos) {
                            RepoItem(repo = it, onClick = { onRepoClick(it.htmlUrl) })
                        }
                    }
                }
            }
        }
    }
}

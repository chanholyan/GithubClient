package com.kaixinchen.githubclient.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kaixinchen.githubclient.data.model.Repo

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    isLoggedIn: Boolean,
    onRepoClick: (String) -> Unit,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("GitHub Explore", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            
            if (isLoggedIn) {
                TextButton(onClick = onLogoutClick) {
                    Text("Sign Out", color = MaterialTheme.colorScheme.error)
                }
            } else {
                Button(onClick = onLoginClick) {
                    Text("Sign In")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search by language (e.g. kotlin)") },
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { viewModel.search() }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (val state = uiState) {
            is SearchUiState.Idle -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Type a programming language to search repositories")
                }
            }
            is SearchUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is SearchUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
            }
            is SearchUiState.Success -> {
                val listTitle = if (searchQuery.isBlank()) "🔥 Popular Repositories" else "🔍 Search Results"
                
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = listTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.repos) { repo ->
                            RepoItem(repo = repo, onClick = { onRepoClick(repo.htmlUrl) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RepoItem(
    repo: Repo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = repo.fullName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            
            if (!repo.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = repo.description, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = "Stars", modifier = Modifier.size(16.dp), tint = Color(0xFFFFC107))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = repo.stargazersCount.toString(), style = MaterialTheme.typography.labelMedium)
                
                Spacer(modifier = Modifier.width(16.dp))
                if (!repo.language.isNullOrBlank()) {
                    Text(text = repo.language, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

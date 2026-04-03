package com.kaixinchen.githubclient.ui.search

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.kaixinchen.githubclient.data.model.Owner
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
            trailingIcon = {
                IconButton(onClick = { viewModel.search() }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            },
            singleLine = true,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
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
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Error",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Oops! Something went wrong.",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = { viewModel.search() },
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                    ) {
                        Text("Try Again")
                    }
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
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp), // 🌟 12dp：不多不少的现代圆角
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface, // 自动适配深浅色模式
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant // 🌟 极细且柔和的边框线
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                // 🌟 Use real developer avatar instead of gray book icon
                AsyncImage(
                    model = repo.owner.avatarUrl,
                    contentDescription = "Owner Avatar",
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .border(
                            width = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant,
                            shape = CircleShape
                        )
                )
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = repo.fullName,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (!repo.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = repo.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, // 次要文字颜色
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.2f, // 稍微增加行高，提升阅读体验
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.StarBorder,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = repo.stargazersCount.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.width(16.dp)) // 拉开一点间距

                if (!repo.language.isNullOrBlank()) {
                    Surface(
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f) // 半透明底色，不抢眼
                    ) {
                        Text(
                            text = repo.language,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RepoItemPreview() {
    MaterialTheme {
        RepoItem(
            repo = Repo(
                id = 1,
                name = "kotlin",
                fullName = "JetBrains/kotlin",
                description = "The Kotlin Programming Language.",
                htmlUrl = "https://github.com/JetBrains/kotlin",
                stargazersCount = 45000,
                language = "Kotlin",
                owner = Owner(
                    login = "JetBrains",
                    avatarUrl = "https://avatars.githubusercontent.com/u/878435?v=4"
                )
            ),
            onClick = {}
        )
    }
}

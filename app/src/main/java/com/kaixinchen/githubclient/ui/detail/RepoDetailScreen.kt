package com.kaixinchen.githubclient.ui.detail

import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun RepoDetailScreen(
    url: String,
    viewModel: RepoDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val submitState by viewModel.submitState.collectAsState()
    
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(submitState) {
        when (submitState) {
            is IssueSubmitState.Success -> {
                Toast.makeText(context, "Issue created successfully!", Toast.LENGTH_SHORT).show()
                showDialog = false
                viewModel.resetState()
            }
            is IssueSubmitState.Error -> {
                val errorMsg = (submitState as IssueSubmitState.Error).message
                Toast.makeText(context, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        floatingActionButton = {
            if (viewModel.isLoggedIn) {
                FloatingActionButton(
                    onClick = { showDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Raise Issue")
                }
            }
        }
    ) {
        innerPadding ->
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            factory = { ctx ->
                WebView(ctx).apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    loadUrl(url)
                }
            },
            update = {
            }
        )
    }

    if (showDialog) {
        var title by remember { mutableStateOf("") }
        var body by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = {
                if (submitState !is IssueSubmitState.Loading) showDialog = false
            },
            title = { Text("Raise an Issue") },
            text = {
                Column {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Issue Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = body,
                        onValueChange = { body = it },
                        label = { Text("Description (Optional)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 5
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.submitIssue(url, title, body) },
                    enabled = title.isNotBlank() && submitState !is IssueSubmitState.Loading
                ) {
                    if (submitState is IssueSubmitState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                    } else {
                        Text("Submit")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false },
                    enabled = submitState !is IssueSubmitState.Loading
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

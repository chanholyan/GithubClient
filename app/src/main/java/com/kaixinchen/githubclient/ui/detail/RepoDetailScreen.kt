package com.kaixinchen.githubclient.ui.detail

import android.webkit.*
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
        var isLoading by remember { mutableStateOf(true) }
        var isError by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf("") }
        var webViewInstance by remember { mutableStateOf<WebView?>(null) }
        
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                factory = { ctx ->
                    val webView = WebView(ctx)
                    webViewInstance = webView
                    
                    class MyWebViewClient : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            isLoading = true
                            isError = false
                        }
                        
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            isLoading = false
                            isError = false
                        }
                        
                        override fun onReceivedError(
                            view: WebView?, 
                            request: WebResourceRequest?, 
                            error: WebResourceError?
                        ) {
                            super.onReceivedError(view, request, error)
                            isLoading = false
                            isError = true
                            errorMessage = error?.description?.toString() ?: "Unknown error"
                        }
                        
                        override fun shouldOverrideUrlLoading(
                            view: WebView?, 
                            request: WebResourceRequest?
                        ): Boolean {
                            return false
                        }
                    }
                    
                    webView.webViewClient = MyWebViewClient()
                    
                    webView.settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        databaseEnabled = true
                        allowContentAccess = true
                        allowFileAccess = true
                        setSupportZoom(true)
                        builtInZoomControls = true
                        displayZoomControls = false
                        javaScriptCanOpenWindowsAutomatically = true
                        mediaPlaybackRequiresUserGesture = false
                        
                        userAgentString = "Mozilla/5.0 (Linux; Android 10; SM-G975F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.102 Mobile Safari/537.36"
                    }
                    
                    CookieManager.getInstance().setAcceptCookie(true)
                    
                    webView.loadUrl(url)
                    webView
                },
                update = {
                }
            )
            
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            if (isError) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Failed to load page",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { 
                        isLoading = true
                        isError = false
                        webViewInstance?.reload()
                    }) {
                        Text("Retry")
                    }
                }
            }
        }
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

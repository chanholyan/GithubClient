package com.kaixinchen.githubclient

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.kaixinchen.githubclient.data.remote.GithubApiService
import com.kaixinchen.githubclient.ui.theme.GithubClientTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var apiService: GithubApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // add it temperately for api connection testing
        lifecycleScope.launch {
            try {
                Log.d("API_TEST", "Starting GitHub API request...")
                val response = apiService.searchRepos(query = "language:kotlin")
                Log.d("API_TEST", "Request succeeded! Found ${response.totalCount} repositories")
                Log.d("API_TEST", "First repository: ${response.items.firstOrNull()?.fullName}")
            } catch (e: Exception) {
                Log.e("API_TEST", "Request failed: ${e.message}", e)
            }
        }

        setContent {
            GithubClientTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GithubClientTheme {
        Greeting("Android")
    }
}
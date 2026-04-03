package com.kaixinchen.githubclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import com.kaixinchen.githubclient.data.local.AuthManager
import com.kaixinchen.githubclient.ui.auth.LoginScreen
import com.kaixinchen.githubclient.ui.search.SearchScreen
import com.kaixinchen.githubclient.ui.detail.RepoDetailScreen
import com.kaixinchen.githubclient.ui.theme.GithubClientTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var authManager: AuthManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GithubClientTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // Dynamically determine starting page
                    val startRoute = if (authManager.isLoggedIn()) "search" else "login"

                    NavHost(navController = navController, startDestination = startRoute) {
                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    // After successful login, navigate to search page and clear login page from stack
                                    // This prevents going back to login page when pressing back button
                                    navController.navigate("search") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("search") {
                            SearchScreen(
                                onRepoClick = { url ->
                                    // URLs contain special characters (like //) that break navigation routes
                                    // So must encode it first before passing to the detail screen
                                    val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                                    navController.navigate("detail/$encodedUrl")
                                },
                                onLogout = {
                                    navController.navigate("login") {
                                        // Clear all history from stack when navigating back to login
                                        // This prevents going back to main page when pressing back button
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(
                            route = "detail/{url}",
                            arguments = listOf(navArgument("url") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val encodedUrl = backStackEntry.arguments?.getString("url") ?: ""
                            val decodedUrl = URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8.toString())
                        
                            RepoDetailScreen(url = decodedUrl)
                        }
                    }
                }
            }
        }
    }
}

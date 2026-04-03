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
import com.kaixinchen.githubclient.ui.search.SearchScreen
import com.kaixinchen.githubclient.ui.detail.RepoDetailScreen
import com.kaixinchen.githubclient.ui.theme.GithubClientTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GithubClientTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "search") {
                        composable("search") {
                            SearchScreen(
                                onRepoClick = { url ->
                                    // URLs contain special characters (like //) that break navigation routes
                                    // So must encode it first before passing to the detail screen
                                    val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                                    navController.navigate("detail/$encodedUrl")
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

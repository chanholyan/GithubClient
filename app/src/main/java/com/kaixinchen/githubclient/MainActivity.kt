package com.kaixinchen.githubclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import com.kaixinchen.githubclient.data.local.AuthManager
import com.kaixinchen.githubclient.ui.auth.LoginScreen
import com.kaixinchen.githubclient.ui.search.SearchScreen
import com.kaixinchen.githubclient.ui.detail.RepoDetailScreen
import com.kaixinchen.githubclient.ui.profile.ProfileScreen
import com.kaixinchen.githubclient.ui.theme.GithubClientTheme

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
                    var isLoggedIn by remember { mutableStateOf(authManager.isLoggedIn()) }
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "main_flow") {
                        
                        composable("main_flow") {
                            MainScreen(
                                isLoggedIn = isLoggedIn,
                                onNavigateToLogin = { navController.navigate("login") },
                                onNavigateToDetail = { url ->
                                    val encoded = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                                    navController.navigate("detail/$encoded")
                                },
                                onLogout = {
                                    authManager.clearToken()
                                    isLoggedIn = false
                                    navController.navigate("main_flow") { popUpTo(0) { inclusive = true } }
                                }
                            )
                        }

                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    isLoggedIn = true
                                    navController.popBackStack() 
                                },
                                onSkipLogin = {
                                    navController.popBackStack()
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

@Composable
fun MainScreen(
    isLoggedIn: Boolean,
    onNavigateToLogin: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onLogout: () -> Unit
) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Search, contentDescription = "Explore") },
                    label = { Text("Explore") },
                    selected = currentRoute == "search",
                    onClick = {
                        bottomNavController.navigate("search") {
                            popUpTo(bottomNavController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = currentRoute == "profile",
                    onClick = {
                        bottomNavController.navigate("profile") {
                            popUpTo(bottomNavController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) {
        innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = "search",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("search") {
                SearchScreen(
                    isLoggedIn = isLoggedIn, 
                    onRepoClick = onNavigateToDetail,
                    onLoginClick = onNavigateToLogin,
                    onLogoutClick = onLogout
                )
            }
            composable("profile") {
                ProfileScreen(
                    isLoggedIn = isLoggedIn,
                    onRepoClick = onNavigateToDetail,
                    onLoginClick = onNavigateToLogin
                )
            }
        }
    }
}

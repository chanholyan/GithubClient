package com.kaixinchen.githubclient.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kaixinchen.githubclient.ui.detail.RepoDetailScreen
import com.kaixinchen.githubclient.ui.search.SearchScreen

sealed class Screen(val route: String) {
    object Search : Screen("search")
    object Detail : Screen("detail/{url}") {
        fun createRoute(url: String) = "detail/$url"
    }
}

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Search.route) {
        composable(Screen.Search.route) {
            SearchScreen(
                onRepoClick = { url ->
                    navController.navigate(Screen.Detail.createRoute(url))
                }
            )
        }
        composable(Screen.Detail.route) {backStackEntry ->
            val url = backStackEntry.arguments?.getString("url") ?: ""
            RepoDetailScreen(url = url)
        }
    }
}

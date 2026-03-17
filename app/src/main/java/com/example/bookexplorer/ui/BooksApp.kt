package com.example.bookexplorer.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bookexplorer.navigation.Details
import com.example.bookexplorer.navigation.Favorites
import com.example.bookexplorer.navigation.Home
import com.example.bookexplorer.navigation.ReadList
import com.example.bookexplorer.ui.screens.BookDetailScreen
import com.example.bookexplorer.ui.screens.BooksScreen
import com.example.bookexplorer.ui.screens.FavoritesScreen
import com.example.bookexplorer.ui.screens.ReadListScreen


@Composable
fun BooksApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    val booksViewmodel: BooksViewModel = viewModel(factory = BooksViewModel.Factory)
    val uiState by booksViewmodel.uiState.collectAsState()

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val showBottomBar = !isDetailRoute(currentRoute)

    Scaffold(
        bottomBar = { if (showBottomBar) BottomBar(navController) },
        modifier = modifier
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            booksViewmodel = booksViewmodel,
            uiState = uiState,
            modifier = Modifier.padding(innerPadding)
        )
    }
}


sealed class BottomTab(val route: String, val label: String, val icon: ImageVector) {
    data object Discover : BottomTab(Home.route, "Discover", Icons.Default.Search)
    data object FavoritesTab : BottomTab(Favorites.route, "Favorites", Icons.Default.Favorite)
    data object ReadListTab : BottomTab(ReadList.route, "Readlist", Icons.Default.Bookmark)
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    booksViewmodel: BooksViewModel,
    uiState: BooksUiState,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Home.route,
        modifier = modifier
    ) {
        composable(Home.route) {
            BooksScreen(
                booksUiState = uiState,
                onLoadMore = { booksViewmodel.loadMore() },
                onClickBook = { id -> navController.navigate("${Details.route}/$id") },
                onSearch = { q -> booksViewmodel.search(q) }
            )
        }

        composable(Favorites.route) {
            FavoritesScreen(
                onClickBook = { id -> navController.navigate("${Details.route}/$id") }
            )
        }

        composable(ReadList.route) {
            ReadListScreen(
                onClickBook = { id -> navController.navigate("${Details.route}/$id") }
            )
        }

        composable(
            route = Details.routeWithArgs,
            arguments = Details.arguments
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getInt(Details.bookIdArg) ?: return@composable
            val book = booksViewmodel.findBookById(bookId)

            BookDetailScreen(
                bookId = bookId,
                book = book,
                onBack = { navController.popBackStack() }
            )

        }
    }
}

val bottomTabs = listOf(BottomTab.Discover, BottomTab.FavoritesTab, BottomTab.ReadListTab)

@Composable
fun BottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val destination = navBackStackEntry?.destination

    NavigationBar {
        bottomTabs.forEach { tab ->
            val selected = destination?.hierarchy?.any { it.route == tab.route } == true

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(tab.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(Home.route) { saveState = true }
                    }
                },
                icon = { Icon(tab.icon, contentDescription = tab.label) },
                label = { Text(tab.label) }
            )
        }
    }
}


private fun isDetailRoute(route: String?): Boolean {
    // When arguments are present, current route can be "detail/{bookId}" or sometimes resolved.
    // The safest check is prefix match.
    return route?.startsWith(Details.route) == true
}


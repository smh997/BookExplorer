package com.example.bookexplorer.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

interface BookDestination { val route: String }

object Home : BookDestination { override val route = "home" }
object Favorites : BookDestination { override val route = "favorites" }
object ReadList : BookDestination { override val route = "readlist" }

object Details : BookDestination {
    override val route = "detail"
    const val bookIdArg = "bookId"
    val routeWithArgs = "$route/{$bookIdArg}"
    val arguments = listOf(navArgument(bookIdArg) { type = NavType.IntType })
}
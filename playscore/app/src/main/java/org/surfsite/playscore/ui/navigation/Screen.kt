package org.surfsite.playscore.ui.navigation

sealed class Screen(val route: String) {
    data object Players : Screen("players")

    data object Game : Screen("game/{gameId}") {
        fun createRoute(gameId: Long) = "game/$gameId"
    }

    data object Results : Screen("results/{gameId}") {
        fun createRoute(gameId: Long) = "results/$gameId"
    }

    data object History : Screen("history")
}

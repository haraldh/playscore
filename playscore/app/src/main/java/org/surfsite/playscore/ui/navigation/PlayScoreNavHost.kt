package org.surfsite.playscore.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.surfsite.playscore.ui.game.GameScreen
import org.surfsite.playscore.ui.history.HistoryScreen
import org.surfsite.playscore.ui.players.PlayersScreen
import org.surfsite.playscore.ui.results.ResultsScreen

@Composable
fun PlayScoreNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Players.route,
        modifier = modifier
    ) {
        composable(Screen.Players.route) {
            PlayersScreen(
                onStartGame = { gameId ->
                    navController.navigate(Screen.Game.createRoute(gameId))
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                }
            )
        }

        composable(
            route = Screen.Game.route,
            arguments = listOf(navArgument("gameId") { type = NavType.LongType })
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getLong("gameId") ?: return@composable
            GameScreen(
                gameId = gameId,
                onGameEnded = { endedGameId ->
                    navController.navigate(Screen.Results.createRoute(endedGameId)) {
                        popUpTo(Screen.Players.route)
                    }
                }
            )
        }

        composable(
            route = Screen.Results.route,
            arguments = listOf(navArgument("gameId") { type = NavType.LongType })
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getLong("gameId") ?: return@composable
            ResultsScreen(
                gameId = gameId,
                onNewGame = {
                    navController.navigate(Screen.Players.route) {
                        popUpTo(Screen.Players.route) { inclusive = true }
                    }
                },
                onViewHistory = {
                    navController.navigate(Screen.History.route)
                }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

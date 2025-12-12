package org.surfsite.playscore.ui.results

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.surfsite.playscore.PlayScoreApplication
import org.surfsite.playscore.ui.theme.WinnerGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    gameId: Long,
    onNewGame: () -> Unit,
    onViewHistory: () -> Unit,
    viewModel: ResultsViewModel = viewModel(
        factory = ResultsViewModel.Factory(
            gameId = gameId,
            gameRepository = (LocalContext.current.applicationContext as PlayScoreApplication).gameRepository
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Results") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Winner card (first place)
                    val winners = uiState.rankedParticipants.filter { it.rank == 1 }
                    if (winners.isNotEmpty()) {
                        item {
                            WinnerCard(winners = winners)
                        }
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    // All rankings
                    items(uiState.rankedParticipants) { rankedPlayer ->
                        RankingRow(rankedPlayer = rankedPlayer)
                    }

                    // Action buttons
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onNewGame,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("New Game")
                            }
                            OutlinedButton(
                                onClick = onViewHistory,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    Icons.Default.History,
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text("View History")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WinnerCard(winners: List<RankedPlayer>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = WinnerGold.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.EmojiEvents,
                contentDescription = "Winner",
                modifier = Modifier.size(48.dp),
                tint = WinnerGold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (winners.size == 1) "Winner!" else "Winners!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            winners.forEach { winner ->
                Text(
                    text = winner.player.playerName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "${winners.first().player.score} points",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RankingRow(rankedPlayer: RankedPlayer) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (rankedPlayer.isWinner) {
                WinnerGold.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank
            Text(
                text = "#${rankedPlayer.rank}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = when (rankedPlayer.rank) {
                    1 -> WinnerGold
                    2 -> Color(0xFFC0C0C0) // Silver
                    3 -> Color(0xFFCD7F32) // Bronze
                    else -> MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier.padding(end = 16.dp)
            )

            // Player name
            Text(
                text = rankedPlayer.player.playerName,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            // Score
            Text(
                text = rankedPlayer.player.score.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

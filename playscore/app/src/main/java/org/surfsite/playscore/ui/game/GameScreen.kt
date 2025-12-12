package org.surfsite.playscore.ui.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import org.surfsite.playscore.PlayScoreApplication
import org.surfsite.playscore.data.local.dao.PlayerWithScoreDto
import org.surfsite.playscore.ui.components.AddScoreDialog
import org.surfsite.playscore.ui.components.ConfirmationDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    gameId: Long,
    onGameEnded: (Long) -> Unit,
    viewModel: GameViewModel = viewModel(
        factory = GameViewModel.Factory(
            gameId = gameId,
            gameRepository = (LocalContext.current.applicationContext as PlayScoreApplication).gameRepository
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    var showSortMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Game") },
                actions = {
                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort")
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            SortOrder.entries.forEach { order ->
                                DropdownMenuItem(
                                    text = { Text(order.displayName()) },
                                    onClick = {
                                        viewModel.setSortOrder(order)
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = { viewModel.showEndGameConfirmation() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("End Game")
                }
            }
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
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.participants, key = { it.playerId }) { participant ->
                        PlayerScoreCard(
                            participant = participant,
                            onQuickAdd = { viewModel.addScore(participant.playerId, 1) },
                            onQuickSubtract = { viewModel.addScore(participant.playerId, -1) },
                            onCustomScore = { viewModel.showScoreDialog(participant) }
                        )
                    }
                }
            }
        }
    }

    // Dialogs
    uiState.playerForScoreDialog?.let { player ->
        AddScoreDialog(
            playerName = player.playerName,
            onAddScore = { score ->
                viewModel.addScore(player.playerId, score)
                viewModel.hideScoreDialog()
            },
            onDismiss = { viewModel.hideScoreDialog() }
        )
    }

    if (uiState.showEndGameConfirmation) {
        ConfirmationDialog(
            title = "End Game",
            message = "Are you sure you want to end the game? The scores will be saved.",
            confirmText = "End Game",
            onConfirm = {
                scope.launch {
                    val endedGameId = viewModel.endGame()
                    onGameEnded(endedGameId)
                }
            },
            onDismiss = { viewModel.hideEndGameConfirmation() }
        )
    }
}

@Composable
private fun PlayerScoreCard(
    participant: PlayerWithScoreDto,
    onQuickAdd: () -> Unit,
    onQuickSubtract: () -> Unit,
    onCustomScore: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = participant.playerName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = participant.score.toString(),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        participant.score > 0 -> MaterialTheme.colorScheme.primary
                        participant.score < 0 -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalButton(onClick = onQuickSubtract) {
                    Icon(Icons.Default.Remove, contentDescription = "Subtract 1")
                }
                FilledTonalButton(onClick = onQuickAdd) {
                    Icon(Icons.Default.Add, contentDescription = "Add 1")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onCustomScore) {
                    Text("+/-")
                }
            }
        }
    }
}

private fun SortOrder.displayName(): String = when (this) {
    SortOrder.SCORE_HIGH_TO_LOW -> "Score (High to Low)"
    SortOrder.SCORE_LOW_TO_HIGH -> "Score (Low to High)"
    SortOrder.ALPHABETICAL -> "Alphabetical"
    SortOrder.ORIGINAL_ORDER -> "Original Order"
}

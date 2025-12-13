package org.surfsite.playscore.ui.players

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.surfsite.playscore.R
import kotlinx.coroutines.launch
import org.surfsite.playscore.PlayScoreApplication
import org.surfsite.playscore.data.local.entity.Player
import org.surfsite.playscore.ui.components.AddPlayerDialog
import org.surfsite.playscore.ui.components.ConfirmationDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayersScreen(
    onStartGame: (Long) -> Unit,
    onNavigateToHistory: () -> Unit,
    viewModel: PlayersViewModel = viewModel(
        factory = PlayersViewModel.Factory(
            playerRepository = (LocalContext.current.applicationContext as PlayScoreApplication).playerRepository,
            gameRepository = (LocalContext.current.applicationContext as PlayScoreApplication).gameRepository
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.players_title)) },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Default.History, contentDescription = stringResource(R.string.players_history_button))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.showAddDialog() }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.players_add_player))
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.startGame()?.let { gameId ->
                                onStartGame(gameId)
                            }
                        }
                    },
                    enabled = uiState.selectedPlayerIds.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = if (uiState.selectedPlayerIds.isEmpty()) {
                            stringResource(R.string.players_select_to_start)
                        } else {
                            pluralStringResource(R.plurals.players_start_game, uiState.selectedPlayerIds.size, uiState.selectedPlayerIds.size)
                        }
                    )
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
            } else if (uiState.players.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.players_empty_title),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = stringResource(R.string.players_empty_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.players, key = { it.id }) { player ->
                        PlayerRow(
                            player = player,
                            isSelected = player.id in uiState.selectedPlayerIds,
                            onSelectionChanged = { viewModel.togglePlayerSelection(player.id) },
                            onDelete = { viewModel.showDeleteConfirmation(player) }
                        )
                    }
                }
            }
        }
    }

    // Dialogs
    if (uiState.showAddDialog) {
        AddPlayerDialog(
            onAddPlayer = { name -> viewModel.addPlayer(name) },
            onDismiss = { viewModel.hideAddDialog() }
        )
    }

    uiState.playerToDelete?.let { player ->
        ConfirmationDialog(
            title = stringResource(R.string.delete_player_title),
            message = stringResource(R.string.delete_player_message, player.name),
            confirmText = stringResource(R.string.action_delete),
            onConfirm = { viewModel.deletePlayer(player) },
            onDismiss = { viewModel.hideDeleteConfirmation() }
        )
    }
}

@Composable
private fun PlayerRow(
    player: Player,
    isSelected: Boolean,
    onSelectionChanged: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        onClick = onSelectionChanged,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onSelectionChanged() }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = player.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.players_delete),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

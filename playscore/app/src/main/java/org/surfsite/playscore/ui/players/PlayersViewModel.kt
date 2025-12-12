package org.surfsite.playscore.ui.players

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.surfsite.playscore.data.local.entity.Player
import org.surfsite.playscore.data.repository.GameRepository
import org.surfsite.playscore.data.repository.PlayerRepository

data class PlayersUiState(
    val players: List<Player> = emptyList(),
    val selectedPlayerIds: Set<Long> = emptySet(),
    val showAddDialog: Boolean = false,
    val playerToDelete: Player? = null,
    val isLoading: Boolean = true
)

class PlayersViewModel(
    private val playerRepository: PlayerRepository,
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayersUiState())
    val uiState: StateFlow<PlayersUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            playerRepository.getAllPlayers().collect { players ->
                _uiState.update { it.copy(players = players, isLoading = false) }
            }
        }
    }

    fun togglePlayerSelection(playerId: Long) {
        _uiState.update { state ->
            val newSelection = if (playerId in state.selectedPlayerIds) {
                state.selectedPlayerIds - playerId
            } else {
                state.selectedPlayerIds + playerId
            }
            state.copy(selectedPlayerIds = newSelection)
        }
    }

    fun showAddDialog() {
        _uiState.update { it.copy(showAddDialog = true) }
    }

    fun hideAddDialog() {
        _uiState.update { it.copy(showAddDialog = false) }
    }

    fun addPlayer(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            playerRepository.addPlayer(name)
            hideAddDialog()
        }
    }

    fun showDeleteConfirmation(player: Player) {
        _uiState.update { it.copy(playerToDelete = player) }
    }

    fun hideDeleteConfirmation() {
        _uiState.update { it.copy(playerToDelete = null) }
    }

    fun deletePlayer(player: Player) {
        viewModelScope.launch {
            playerRepository.deletePlayer(player.id)
            _uiState.update { state ->
                state.copy(
                    selectedPlayerIds = state.selectedPlayerIds - player.id,
                    playerToDelete = null
                )
            }
        }
    }

    suspend fun startGame(): Long? {
        val selectedIds = _uiState.value.selectedPlayerIds.toList()
        if (selectedIds.isEmpty()) return null
        return gameRepository.startGame(selectedIds)
    }

    class Factory(
        private val playerRepository: PlayerRepository,
        private val gameRepository: GameRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PlayersViewModel(playerRepository, gameRepository) as T
        }
    }
}

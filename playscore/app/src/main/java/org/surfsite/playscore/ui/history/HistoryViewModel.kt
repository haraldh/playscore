package org.surfsite.playscore.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.surfsite.playscore.data.local.entity.Game
import org.surfsite.playscore.data.repository.GameRepository
import org.surfsite.playscore.data.repository.GameWithParticipants

data class HistoryUiState(
    val games: List<GameWithParticipants> = emptyList(),
    val expandedGameIds: Set<Long> = emptySet(),
    val isLoading: Boolean = true,
    val gameToDelete: GameWithParticipants? = null
)

class HistoryViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            gameRepository.getCompletedGames().collect { games ->
                val gamesWithParticipants = games.mapNotNull { game ->
                    gameRepository.getGameWithParticipants(game.id)
                }
                _uiState.update {
                    it.copy(
                        games = gamesWithParticipants,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun toggleExpanded(gameId: Long) {
        _uiState.update { state ->
            val newExpanded = if (gameId in state.expandedGameIds) {
                state.expandedGameIds - gameId
            } else {
                state.expandedGameIds + gameId
            }
            state.copy(expandedGameIds = newExpanded)
        }
    }

    fun showDeleteConfirmation(game: GameWithParticipants) {
        _uiState.update { it.copy(gameToDelete = game) }
    }

    fun hideDeleteConfirmation() {
        _uiState.update { it.copy(gameToDelete = null) }
    }

    fun deleteGame() {
        val game = _uiState.value.gameToDelete ?: return
        viewModelScope.launch {
            gameRepository.deleteGame(game.game.id)
            _uiState.update { it.copy(gameToDelete = null) }
        }
    }

    class Factory(
        private val gameRepository: GameRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HistoryViewModel(gameRepository) as T
        }
    }
}

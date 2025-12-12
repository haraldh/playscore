package org.surfsite.playscore.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.surfsite.playscore.data.local.dao.PlayerWithScoreDto
import org.surfsite.playscore.data.repository.GameRepository

enum class SortOrder {
    SCORE_HIGH_TO_LOW,
    SCORE_LOW_TO_HIGH,
    ALPHABETICAL,
    ORIGINAL_ORDER
}

data class GameUiState(
    val gameId: Long = 0,
    val participants: List<PlayerWithScoreDto> = emptyList(),
    val sortOrder: SortOrder = SortOrder.ORIGINAL_ORDER,
    val playerForScoreDialog: PlayerWithScoreDto? = null,
    val showEndGameConfirmation: Boolean = false,
    val isLoading: Boolean = true
)

class GameViewModel(
    private val gameId: Long,
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState(gameId = gameId))
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            gameRepository.getParticipantsWithScores(gameId).collect { participants ->
                _uiState.update { state ->
                    state.copy(
                        participants = sortParticipants(participants, state.sortOrder),
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun sortParticipants(
        participants: List<PlayerWithScoreDto>,
        sortOrder: SortOrder
    ): List<PlayerWithScoreDto> {
        return when (sortOrder) {
            SortOrder.SCORE_HIGH_TO_LOW -> participants.sortedByDescending { it.score }
            SortOrder.SCORE_LOW_TO_HIGH -> participants.sortedBy { it.score }
            SortOrder.ALPHABETICAL -> participants.sortedBy { it.playerName.lowercase() }
            SortOrder.ORIGINAL_ORDER -> participants.sortedBy { it.displayOrder }
        }
    }

    fun setSortOrder(order: SortOrder) {
        _uiState.update { state ->
            state.copy(
                sortOrder = order,
                participants = sortParticipants(state.participants, order)
            )
        }
    }

    fun addScore(playerId: Long, delta: Int) {
        viewModelScope.launch {
            gameRepository.updateScore(gameId, playerId, delta)
        }
    }

    fun showScoreDialog(player: PlayerWithScoreDto) {
        _uiState.update { it.copy(playerForScoreDialog = player) }
    }

    fun hideScoreDialog() {
        _uiState.update { it.copy(playerForScoreDialog = null) }
    }

    fun showEndGameConfirmation() {
        _uiState.update { it.copy(showEndGameConfirmation = true) }
    }

    fun hideEndGameConfirmation() {
        _uiState.update { it.copy(showEndGameConfirmation = false) }
    }

    suspend fun endGame(): Long {
        gameRepository.endGame(gameId)
        return gameId
    }

    class Factory(
        private val gameId: Long,
        private val gameRepository: GameRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GameViewModel(gameId, gameRepository) as T
        }
    }
}

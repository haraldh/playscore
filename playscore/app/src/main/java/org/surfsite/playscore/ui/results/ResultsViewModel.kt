package org.surfsite.playscore.ui.results

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
import org.surfsite.playscore.data.repository.GameWithParticipants

data class ResultsUiState(
    val gameId: Long = 0,
    val game: GameWithParticipants? = null,
    val rankedParticipants: List<RankedPlayer> = emptyList(),
    val isLoading: Boolean = true
)

data class RankedPlayer(
    val rank: Int,
    val player: PlayerWithScoreDto,
    val isWinner: Boolean
)

class ResultsViewModel(
    private val gameId: Long,
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResultsUiState(gameId = gameId))
    val uiState: StateFlow<ResultsUiState> = _uiState.asStateFlow()

    init {
        loadResults()
    }

    private fun loadResults() {
        viewModelScope.launch {
            val gameWithParticipants = gameRepository.getGameWithParticipants(gameId)
            if (gameWithParticipants != null) {
                val rankedPlayers = calculateRankings(gameWithParticipants.participants)
                _uiState.update {
                    it.copy(
                        game = gameWithParticipants,
                        rankedParticipants = rankedPlayers,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun calculateRankings(participants: List<PlayerWithScoreDto>): List<RankedPlayer> {
        if (participants.isEmpty()) return emptyList()

        // Sort by score descending
        val sorted = participants.sortedByDescending { it.score }
        val highestScore = sorted.first().score

        var currentRank = 1
        var previousScore: Int? = null

        return sorted.mapIndexed { index, player ->
            if (previousScore != null && player.score != previousScore) {
                currentRank = index + 1
            }
            previousScore = player.score

            RankedPlayer(
                rank = currentRank,
                player = player,
                isWinner = player.score == highestScore
            )
        }
    }

    class Factory(
        private val gameId: Long,
        private val gameRepository: GameRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ResultsViewModel(gameId, gameRepository) as T
        }
    }
}

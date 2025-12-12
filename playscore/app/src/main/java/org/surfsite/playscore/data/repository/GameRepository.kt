package org.surfsite.playscore.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.surfsite.playscore.data.local.dao.GameDao
import org.surfsite.playscore.data.local.dao.PlayerDao
import org.surfsite.playscore.data.local.dao.PlayerWithScoreDto
import org.surfsite.playscore.data.local.entity.Game
import org.surfsite.playscore.data.local.entity.GameParticipant

class GameRepository(
    private val gameDao: GameDao,
    private val playerDao: PlayerDao
) {

    suspend fun startGame(playerIds: List<Long>): Long {
        val game = Game()
        val gameId = gameDao.insertGame(game)

        val participants = playerIds.mapIndexed { index, playerId ->
            GameParticipant(
                gameId = gameId,
                playerId = playerId,
                score = 0,
                displayOrder = index
            )
        }
        gameDao.insertParticipants(participants)

        return gameId
    }

    suspend fun endGame(gameId: Long) {
        gameDao.endGame(gameId)
    }

    suspend fun getGameById(gameId: Long): Game? = gameDao.getGameById(gameId)

    suspend fun getActiveGame(): Game? = gameDao.getActiveGame()

    fun getParticipantsWithScores(gameId: Long): Flow<List<PlayerWithScoreDto>> =
        gameDao.getParticipantsWithNames(gameId)

    suspend fun getParticipantsWithScoresOnce(gameId: Long): List<PlayerWithScoreDto> =
        gameDao.getParticipantsWithNamesOnce(gameId)

    suspend fun updateScore(gameId: Long, playerId: Long, delta: Int) {
        gameDao.updateScore(gameId, playerId, delta)
    }

    fun getCompletedGames(): Flow<List<Game>> = gameDao.getCompletedGames()

    suspend fun getGameWithParticipants(gameId: Long): GameWithParticipants? {
        val game = gameDao.getGameById(gameId) ?: return null
        val participants = gameDao.getParticipantsWithNamesOnce(gameId)
        return GameWithParticipants(
            game = game,
            participants = participants
        )
    }
}

data class GameWithParticipants(
    val game: Game,
    val participants: List<PlayerWithScoreDto>
)

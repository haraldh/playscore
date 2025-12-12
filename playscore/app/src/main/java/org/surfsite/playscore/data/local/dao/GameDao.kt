package org.surfsite.playscore.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import org.surfsite.playscore.data.local.entity.Game
import org.surfsite.playscore.data.local.entity.GameParticipant

@Dao
interface GameDao {
    // Game operations
    @Insert
    suspend fun insertGame(game: Game): Long

    @Query("UPDATE games SET endedAt = :endedAt, isActive = 0 WHERE id = :gameId")
    suspend fun endGame(gameId: Long, endedAt: Long = System.currentTimeMillis())

    @Query("SELECT * FROM games WHERE id = :gameId")
    suspend fun getGameById(gameId: Long): Game?

    @Query("SELECT * FROM games WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveGame(): Game?

    @Query("SELECT * FROM games WHERE isActive = 0 ORDER BY endedAt DESC")
    fun getCompletedGames(): Flow<List<Game>>

    @Query("DELETE FROM games WHERE id = :gameId")
    suspend fun deleteGame(gameId: Long)

    // GameParticipant operations
    @Insert
    suspend fun insertParticipant(participant: GameParticipant)

    @Insert
    suspend fun insertParticipants(participants: List<GameParticipant>)

    @Query("UPDATE game_participants SET score = score + :delta WHERE gameId = :gameId AND playerId = :playerId")
    suspend fun updateScore(gameId: Long, playerId: Long, delta: Int)

    @Query("SELECT * FROM game_participants WHERE gameId = :gameId ORDER BY displayOrder ASC")
    fun getParticipantsForGame(gameId: Long): Flow<List<GameParticipant>>

    @Query("SELECT * FROM game_participants WHERE gameId = :gameId ORDER BY displayOrder ASC")
    suspend fun getParticipantsForGameOnce(gameId: Long): List<GameParticipant>

    // Combined queries
    @Query("""
        SELECT gp.gameId, gp.playerId, p.name as playerName, gp.score, gp.displayOrder
        FROM game_participants gp
        INNER JOIN players p ON gp.playerId = p.id
        WHERE gp.gameId = :gameId
        ORDER BY gp.displayOrder ASC
    """)
    fun getParticipantsWithNames(gameId: Long): Flow<List<PlayerWithScoreDto>>

    @Query("""
        SELECT gp.gameId, gp.playerId, p.name as playerName, gp.score, gp.displayOrder
        FROM game_participants gp
        INNER JOIN players p ON gp.playerId = p.id
        WHERE gp.gameId = :gameId
        ORDER BY gp.displayOrder ASC
    """)
    suspend fun getParticipantsWithNamesOnce(gameId: Long): List<PlayerWithScoreDto>
}

data class PlayerWithScoreDto(
    val gameId: Long,
    val playerId: Long,
    val playerName: String,
    val score: Int,
    val displayOrder: Int
)

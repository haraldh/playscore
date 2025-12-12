package org.surfsite.playscore.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.surfsite.playscore.data.local.entity.Player

@Dao
interface PlayerDao {
    @Query("SELECT * FROM players ORDER BY name ASC")
    fun getAllPlayers(): Flow<List<Player>>

    @Query("SELECT * FROM players WHERE id = :playerId")
    suspend fun getPlayerById(playerId: Long): Player?

    @Query("SELECT * FROM players WHERE id IN (:playerIds)")
    suspend fun getPlayersByIds(playerIds: List<Long>): List<Player>

    @Insert
    suspend fun insertPlayer(player: Player): Long

    @Delete
    suspend fun deletePlayer(player: Player)

    @Query("DELETE FROM players WHERE id = :playerId")
    suspend fun deletePlayerById(playerId: Long)
}

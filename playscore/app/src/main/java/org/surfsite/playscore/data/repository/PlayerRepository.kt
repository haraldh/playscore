package org.surfsite.playscore.data.repository

import kotlinx.coroutines.flow.Flow
import org.surfsite.playscore.data.local.dao.PlayerDao
import org.surfsite.playscore.data.local.entity.Player

class PlayerRepository(private val playerDao: PlayerDao) {

    fun getAllPlayers(): Flow<List<Player>> = playerDao.getAllPlayers()

    suspend fun getPlayerById(playerId: Long): Player? = playerDao.getPlayerById(playerId)

    suspend fun getPlayersByIds(playerIds: List<Long>): List<Player> =
        playerDao.getPlayersByIds(playerIds)

    suspend fun addPlayer(name: String): Long {
        val player = Player(name = name.trim())
        return playerDao.insertPlayer(player)
    }

    suspend fun deletePlayer(playerId: Long) {
        playerDao.deletePlayerById(playerId)
    }
}

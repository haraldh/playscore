package org.surfsite.playscore.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "game_participants",
    primaryKeys = ["gameId", "playerId"],
    foreignKeys = [
        ForeignKey(
            entity = Game::class,
            parentColumns = ["id"],
            childColumns = ["gameId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Player::class,
            parentColumns = ["id"],
            childColumns = ["playerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("playerId")]
)
data class GameParticipant(
    val gameId: Long,
    val playerId: Long,
    val score: Int = 0,
    val displayOrder: Int = 0
)

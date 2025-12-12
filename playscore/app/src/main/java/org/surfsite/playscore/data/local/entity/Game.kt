package org.surfsite.playscore.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class Game(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startedAt: Long = System.currentTimeMillis(),
    val endedAt: Long? = null,
    val isActive: Boolean = true
)

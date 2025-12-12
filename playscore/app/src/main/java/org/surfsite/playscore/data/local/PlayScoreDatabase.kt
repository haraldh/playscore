package org.surfsite.playscore.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.surfsite.playscore.data.local.dao.GameDao
import org.surfsite.playscore.data.local.dao.PlayerDao
import org.surfsite.playscore.data.local.entity.Game
import org.surfsite.playscore.data.local.entity.GameParticipant
import org.surfsite.playscore.data.local.entity.Player

@Database(
    entities = [Player::class, Game::class, GameParticipant::class],
    version = 1,
    exportSchema = false
)
abstract class PlayScoreDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun gameDao(): GameDao

    companion object {
        @Volatile
        private var INSTANCE: PlayScoreDatabase? = null

        fun getInstance(context: Context): PlayScoreDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlayScoreDatabase::class.java,
                    "playscore_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

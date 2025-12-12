package org.surfsite.playscore

import android.app.Application
import org.surfsite.playscore.data.local.PlayScoreDatabase
import org.surfsite.playscore.data.repository.GameRepository
import org.surfsite.playscore.data.repository.PlayerRepository

class PlayScoreApplication : Application() {
    val database by lazy { PlayScoreDatabase.getInstance(this) }
    val playerRepository by lazy { PlayerRepository(database.playerDao()) }
    val gameRepository by lazy { GameRepository(database.gameDao(), database.playerDao()) }
}

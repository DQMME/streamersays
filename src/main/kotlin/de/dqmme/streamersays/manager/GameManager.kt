package de.dqmme.streamersays.manager

import de.dqmme.streamersays.misc.Challenge
import org.bukkit.entity.Player

class GameManager {
    var running: Boolean = false
    var gameMaster: Player? = null
    var currentChallenge: Challenge? = null
}
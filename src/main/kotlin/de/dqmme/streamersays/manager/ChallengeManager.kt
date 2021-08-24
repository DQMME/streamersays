package de.dqmme.streamersays.manager

import de.dqmme.streamersays.StreamerSays
import de.dqmme.streamersays.misc.Challenge
import org.bukkit.GameMode
import org.bukkit.configuration.file.YamlConfiguration

class ChallengeManager(private val instance: StreamerSays) {
    private var challengeConf: YamlConfiguration

    init {
        challengeConf = YamlConfiguration.loadConfiguration(instance.challengeFile)
    }

    fun addChallenge(challenge: Challenge) {
        challengeConf.set(challenge.name + ".description", challenge.description!!)

        challengeConf.set(challenge.name + ".location", challenge.location!!)

        challengeConf.set(challenge.name + ".gamemode", challenge.gameMode!!.name)

        saveFile()
    }

    fun challengeByName(name: String): Challenge? {
        for (challenge in challenges()) {
            if (challenge.name == name) {
                return challenge
            }
        }
        return null
    }

    fun challenges(): List<Challenge> {
        val challengeList = arrayListOf<Challenge>()

        for (key in challengeConf.getKeys(false)) {
            val description = challengeConf.getString("$key.description")
            val location = challengeConf.getLocation("$key.location")
            val gameMode = parseGameMode(challengeConf.getString("$key.gamemode"))

            challengeList.add(Challenge(key, description, location, gameMode))
        }

        return challengeList
    }

    fun saveFile() {
        challengeConf.save(instance.challengeFile)
    }

    fun reloadFile() {
        challengeConf = YamlConfiguration.loadConfiguration(instance.challengeFile)
    }

    private fun parseGameMode(name: String?): GameMode? {
        if (name == null) {
            return null
        }

        var gameMode: GameMode? = null
        if (name.lowercase() == "survival") {
            gameMode = GameMode.SURVIVAL
        } else if (name.lowercase() == "creative") {
            gameMode = GameMode.CREATIVE
        } else if (name.lowercase() == "adventure") {
            gameMode = GameMode.ADVENTURE
        } else if (name.lowercase() == "spectator") {
            gameMode = GameMode.SPECTATOR
        }
        return gameMode
    }
}
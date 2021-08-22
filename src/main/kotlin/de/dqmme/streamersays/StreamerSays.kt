package de.dqmme.streamersays

import de.dqmme.streamersays.command.ChallengeCommand
import de.dqmme.streamersays.command.GameMasterCommand
import de.dqmme.streamersays.listener.DeathListener
import de.dqmme.streamersays.listener.HungerListener
import de.dqmme.streamersays.listener.InteractListener
import de.dqmme.streamersays.listener.JoinListener
import de.dqmme.streamersays.manager.ChallengeManager
import de.dqmme.streamersays.manager.GameManager
import de.dqmme.streamersays.manager.KitManager
import de.dqmme.streamersays.manager.MessageManager
import net.axay.kspigot.main.KSpigot
import org.bukkit.Bukkit
import java.io.File

class StreamerSays : KSpigot() {
    companion object {
        lateinit var INSTANCE: StreamerSays; private set
    }

    lateinit var challengeFile: File
    lateinit var kitFile: File
    lateinit var messageFile: File

    override fun load() {
        INSTANCE = this
    }

    override fun startup() {
        challengeFile = File(dataFolder, "challenges.yml")
        kitFile = File(dataFolder, "kits.yml")
        messageFile = File(dataFolder, "messages.yml")

        if (!messageFile.exists()) {
            saveResource("messages.yml", false)
        }

        val gameManager = GameManager()

        val challengeManager = ChallengeManager(this)

        challengeManager.saveFile()

        val kitManager = KitManager(this)

        kitManager.saveFile()

        val messageManager = MessageManager(this)

        listenerRegistration(gameManager, messageManager, challengeManager, kitManager)
        commandRegistration(challengeManager, gameManager, messageManager)
    }

    private fun listenerRegistration(
        gameManager: GameManager,
        messageManager: MessageManager,
        challengeManager: ChallengeManager,
        kitManager: KitManager
    ) {
        val pluginManager = Bukkit.getPluginManager()

        pluginManager.registerEvents(DeathListener(messageManager), this)
        pluginManager.registerEvents(HungerListener(), this)
        pluginManager.registerEvents(JoinListener(gameManager, messageManager), this)
        pluginManager.registerEvents(InteractListener(gameManager, challengeManager, kitManager, messageManager), this)
    }

    private fun commandRegistration(
        challengeManager: ChallengeManager,
        gameManager: GameManager,
        messageManager: MessageManager
    ) {
        getCommand("challenge")!!.setExecutor(ChallengeCommand(this, challengeManager, messageManager))
        getCommand("challenge")!!.setTabCompleter(ChallengeCommand(this, challengeManager, messageManager))

        getCommand("gamemaster")!!.setExecutor(GameMasterCommand(gameManager, messageManager))
        getCommand("gamemaster")!!.setTabCompleter(GameMasterCommand(gameManager, messageManager))
    }
}
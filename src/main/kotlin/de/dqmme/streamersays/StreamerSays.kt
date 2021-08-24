package de.dqmme.streamersays

import de.dqmme.streamersays.command.*
import de.dqmme.streamersays.listener.DeathListener
import de.dqmme.streamersays.listener.HungerListener
import de.dqmme.streamersays.listener.InteractListener
import de.dqmme.streamersays.listener.JoinListener
import de.dqmme.streamersays.manager.*
import net.axay.kspigot.main.KSpigot
import org.bukkit.Bukkit
import java.io.File

class StreamerSays : KSpigot() {
    lateinit var challengeFile: File
    lateinit var itemFile: File
    lateinit var kitFile: File
    lateinit var messageFile: File

    override fun startup() {
        challengeFile = File(dataFolder, "challenges.yml")
        itemFile = File(dataFolder, "items.yml")
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

        val itemManager = ItemManager(this)

        itemManager.saveFile()

        val messageManager = MessageManager(this)

        listenerRegistration(gameManager, messageManager, challengeManager, kitManager, itemManager)
        commandRegistration(challengeManager, gameManager, messageManager, kitManager, itemManager)
    }

    private fun listenerRegistration(
        gameManager: GameManager,
        messageManager: MessageManager,
        challengeManager: ChallengeManager,
        kitManager: KitManager,
        itemManager: ItemManager
    ) {
        val pluginManager = Bukkit.getPluginManager()

        pluginManager.registerEvents(DeathListener(messageManager), this)
        pluginManager.registerEvents(HungerListener(), this)
        pluginManager.registerEvents(JoinListener(gameManager, messageManager), this)
        pluginManager.registerEvents(InteractListener(gameManager, challengeManager, kitManager, itemManager, messageManager), this)
    }

    private fun commandRegistration(
        challengeManager: ChallengeManager,
        gameManager: GameManager,
        messageManager: MessageManager,
        kitManager: KitManager,
        itemManager: ItemManager
    ) {
        getCommand("challenge")!!.setExecutor(ChallengeCommand(this, challengeManager, messageManager))
        getCommand("challenge")!!.setTabCompleter(ChallengeCommand(this, challengeManager, messageManager))

        getCommand("gamemaster")!!.setExecutor(GameMasterCommand(gameManager, messageManager))
        getCommand("gamemaster")!!.setTabCompleter(GameMasterCommand(gameManager, messageManager))

        getCommand("kit")!!.setExecutor(KitCommand(this, kitManager, messageManager))
        getCommand("kit")!!.setTabCompleter(KitCommand(this, kitManager, messageManager))

        getCommand("item")!!.setExecutor(ItemCommand(this, itemManager, messageManager))
        getCommand("item")!!.setTabCompleter(ItemCommand(this, itemManager, messageManager))

        getCommand("revive")!!.setExecutor(ReviveCommand(gameManager, messageManager))
        getCommand("revive")!!.setTabCompleter(ReviveCommand(gameManager, messageManager))
    }
}
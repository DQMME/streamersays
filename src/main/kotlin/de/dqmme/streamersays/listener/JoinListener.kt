package de.dqmme.streamersays.listener

import de.dqmme.streamersays.manager.GameManager
import de.dqmme.streamersays.manager.MessageManager
import de.dqmme.streamersays.misc.StreamerSaysPlayer
import de.dqmme.streamersays.util.Items
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class JoinListener(private var gameManager: GameManager, private var messageManager: MessageManager) : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player

        player.inventory.clear()

        val streamerSaysPlayer = StreamerSaysPlayer.getPlayer(player)

        streamerSaysPlayer.isAlive = !gameManager.running

        if (streamerSaysPlayer.isAlive) {
            player.sendMessage(messageManager.message("game_joined"))
        } else {
            player.gameMode = GameMode.SPECTATOR
            player.sendMessage(messageManager.message("game_already_running"))
        }

        if (player.hasPermission("streamersays.gamemaster")) {
            if (gameManager.gameMaster == null) {
                gameManager.gameMaster = player
                player.sendMessage(messageManager.message("you_are_gamemaster"))
            } else {
                player.sendMessage(
                    messageManager.message("gamemaster_is")
                        .replace("%player%", gameManager.gameMaster!!.name)
                )
            }
        }

        if (gameManager.gameMaster == player) {
            if (gameManager.running) {
                player.inventory.setItem(4, Items.menuItem())
            } else {
                player.inventory.setItem(8, Items.startItem())
            }
        }
    }
}
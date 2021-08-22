package de.dqmme.streamersays.listener

import de.dqmme.streamersays.manager.MessageManager
import de.dqmme.streamersays.misc.StreamerSaysPlayer
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent

class DeathListener(private val messageManager: MessageManager) : Listener {
    @EventHandler
    fun onDeath(event: EntityDeathEvent) {
        if(event.entity.type != EntityType.PLAYER) return

        val player = event.entity as Player
        val streamerSaysPlayer = StreamerSaysPlayer.getPlayer(player)

        player.gameMode = GameMode.SPECTATOR
        streamerSaysPlayer.isAlive = false

        Bukkit.broadcastMessage(messageManager.message("player_died")
            .replace("%player%", player.name))
    }
}
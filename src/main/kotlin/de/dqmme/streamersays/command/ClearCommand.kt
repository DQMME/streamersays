package de.dqmme.streamersays.command

import de.dqmme.streamersays.manager.MessageManager
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

class ClearCommand(private val messageManager: MessageManager) : TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("streamersays.clear") || !sender.hasPermission("streamersays.*")) {
            sender.sendMessage(messageManager.message("no_permissions"))
            return false
        }

        if (args.size > 1) {
            sender.sendMessage(
                messageManager.message("invalid_usage")
                    .replace("%usage%", "/clear <player>")
            )
            return false
        }

        if (args.isEmpty()) {
            if (sender !is Player) {
                sender.sendMessage(messageManager.message("not_a_player"))
                return false
            }

            sender.inventory.clear()

            for (entity in sender.world.entities) {
                if (entity.type == EntityType.DROPPED_ITEM) {
                    entity.remove()
                }
            }
        } else {
            val player = Bukkit.getPlayer(args[0])

            if (player == null) {
                sender.sendMessage(messageManager.message("player_not_found"))
                return false
            }

            player.inventory.clear()

            for (entity in player.world.entities) {
                if (entity.type == EntityType.DROPPED_ITEM) {
                    entity.remove()
                }
            }
        }

        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String>? {
        return if (args.size > 1) {
            null
        } else {
            arrayListOf("")
        }
    }
}
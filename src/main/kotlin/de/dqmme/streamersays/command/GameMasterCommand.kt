package de.dqmme.streamersays.command

import de.dqmme.streamersays.manager.GameManager
import de.dqmme.streamersays.manager.MessageManager
import de.dqmme.streamersays.util.Items
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.inventory.ItemStack

class GameMasterCommand(private val gameManager: GameManager, private val messageManager: MessageManager) :
    TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("streamersays.gamemaster") || !sender.hasPermission("streamersays.*")) {
            sender.sendMessage(messageManager.message("no_permissions"))
            return false
        }

        if (args.isEmpty()) {
            sender.sendMessage(
                messageManager.message("invalid_usage")
                    .replace("%usage%", "/gamemaster <set>")
            )
            return false
        }

        when (args[0].lowercase()) {
            "set" -> {
                if (args.size != 2) {
                    sender.sendMessage(
                        messageManager.message("invalid_usage")
                            .replace("%usage%", "/gamemaster set <player>")
                    )
                    return false
                }

                val player = Bukkit.getPlayer(args[1])

                if (player == null) {
                    sender.sendMessage(messageManager.message("player_not_found"))
                    return false
                }

                if (gameManager.running) {
                    gameManager.gameMaster!!.inventory.setItem(4, ItemStack(Material.AIR))
                    player.inventory.clear()
                    player.inventory.setItem(4, Items.menuItem())
                } else {
                    gameManager.gameMaster!!.inventory.setItem(8, ItemStack(Material.AIR))
                    player.inventory.clear()
                    player.inventory.setItem(8, Items.startItem())
                }

                gameManager.gameMaster!!.sendMessage(messageManager.message("no_longer_gamemaster"))

                gameManager.gameMaster = player

                player.sendMessage(messageManager.message("you_are_gamemaster"))

                sender.sendMessage(
                    messageManager.message(
                        "gamemaster_set"
                            .replace("%player%", player.name)
                    )
                )
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
        if (args.size == 1) {
            return arrayListOf("set")
        }

        if (args.size == 2 && args[0].lowercase() == "set") {
            return null
        }

        return arrayListOf("")
    }
}
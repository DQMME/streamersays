package de.dqmme.streamersays.command

import de.dqmme.streamersays.manager.MessageManager
import de.dqmme.streamersays.util.ItemBuilder
import net.axay.kspigot.gui.GUIType
import net.axay.kspigot.gui.Slots
import net.axay.kspigot.gui.kSpigotGUI
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class KitCommand(private val messageManager: MessageManager) : TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("streamersays.kit")) {
            sender.sendMessage(messageManager.message("no_permissions"))
            return false
        }

        if (args.isEmpty()) {
            sender.sendMessage(
                messageManager.message("invalid_usage")
                    .replace("%prefix%", "/kit <add>")
            )
            return false
        }

        when (args[0].lowercase()) {
            "add" -> {
                if (sender !is Player) {
                    sender.sendMessage(messageManager.message("not_a_player"))
                    return false
                }

                if (args.size != 1) {
                    sender.sendMessage(
                        messageManager.message("invalid_usage")
                            .replace("%usage%", "/kit add")
                    )
                    return false
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
        return null
    }

    private fun openKitGUI(player: Player) {
        val gui = kSpigotGUI(GUIType.THREE_BY_NINE) {
            title = "§aKit erstellen"

            page(1) {
                placeholder(
                    Slots.All, ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                        .displayName("§c")
                        .build()
                )

                pageChanger(Slots.RowTwoSlotThree, ItemBuilder(Material.NAME_TAG).build(), 2, null, null)
            }
        }
    }
}
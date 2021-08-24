package de.dqmme.streamersays.command

import de.dqmme.streamersays.StreamerSays
import de.dqmme.streamersays.manager.ItemManager
import de.dqmme.streamersays.manager.MessageManager
import de.dqmme.streamersays.misc.Emoji
import de.dqmme.streamersays.misc.Item
import de.dqmme.streamersays.util.Items
import net.axay.kspigot.gui.*
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ItemCommand(
    private val instance: StreamerSays,
    private val itemManager: ItemManager,
    private val messageManager: MessageManager
) : TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("streamersays.item") || !sender.hasPermission("streamersays.*")) {
            sender.sendMessage(messageManager.message("no_permissions"))
            return false
        }

        if (args.isEmpty()) {
            sender.sendMessage(
                messageManager.message("invalid_usage")
                    .replace("%usage%", "/item <add>")
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
                            .replace("%prefix%", "/item add")
                    )
                    return false
                }

                openItemGUI(sender, null, null)
            }
        }

        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        return if (args.size == 1) {
            arrayListOf("add")
        } else {
            arrayListOf("")
        }
    }

    private fun openItemGUI(player: Player, itemName: String?, itemStack: ItemStack?) {
        var name: String? = itemName
        var item: ItemStack? = itemStack

        val gui = kSpigotGUI(GUIType.THREE_BY_NINE) {
            title = "§aItem erstellen"

            page(1) {
                placeholder(
                    Slots.All, Items.blackGlass()
                )

                pageChanger(
                    Slots.RowTwoSlotFour, itemStack(Material.NAME_TAG) {
                        meta {
                            name = "§aNamen festlegen " + if (name != null) "§a${Emoji.HOOK.string()}" else "§c${Emoji.X.string()}"

                            addLore {
                                +"§7Setze den §aNamen §7des Items."
                                +"§aAktuell: §f${(name ?: "§7N/A")}"
                            }
                        }
                    },
                    2, null, null
                )

                pageChanger(
                    Slots.RowTwoSlotSix, itemStack(Material.ENDER_CHEST) {
                        meta {
                            name = "§aItem festlegen " + if (item != null) "§a${Emoji.HOOK.string()}" else "§c${Emoji.X.string()}"

                            addLore {
                                +"§7Setze die §aItems §7des Kits."
                                +"§aAktuell: §f${if (itemStack != null) itemStack.itemMeta!!.displayName else "§7N/A"}"
                            }
                        }
                    },
                    3, null, null
                )

                if (name != null && item != null) {
                    button(
                        Slots.RowOneSlotNine, itemStack(Material.LIME_DYE) {
                            meta {
                                name = "§aBestätigen"

                                addLore {
                                    +"§7Klicke dieses Item um §adas Item §7zu speichern."
                                }
                            }
                        }
                    ) {
                        itemManager.addItem(Item(name!!, item!!))

                        player.closeInventory()
                    }
                }
            }

            page(2) {
                transitionFrom = PageChangeEffect.SLIDE_HORIZONTALLY
                transitionTo = PageChangeEffect.SLIDE_HORIZONTALLY

                placeholder(
                    Slots.All, Items.blackGlass()
                )

                button(
                    Slots.RowTwoSlotFive,
                    itemStack(Material.GREEN_CONCRETE) {
                        meta {
                            name = "§aKlick mich"

                            addLore {
                                +"§aKlick §7dieses Item um den Namen einzugeben."
                            }
                        }
                    }
                ) {
                    AnvilGUI.Builder()
                        .preventClose()
                        .onComplete { _: Player, text: String? ->
                            name = text
                            AnvilGUI.Response.close()
                        }
                        .onClose {
                            openItemGUI(player, name, item)
                        }
                        .text("Item-Name")
                        .itemLeft(
                            itemStack(Material.PAPER) {
                                meta {
                                    addLore {
                                        +"§7Trage §aden Namen §7des Items ein."
                                    }
                                }
                            }
                        )
                        .title("§aNamen eingeben")
                        .plugin(instance)
                        .open(player)
                }

                pageChanger(
                    Slots.RowOneSlotOne, Items.mainMenu(),
                    1, null, null
                )
            }

            page(3) {
                transitionFrom = PageChangeEffect.SLIDE_HORIZONTALLY
                transitionTo = PageChangeEffect.SLIDE_HORIZONTALLY

                placeholder(Slots.All, Items.blackGlass())

                freeSlot(Slots.RowTwoSlotFive)

                button(
                    Slots.RowOneSlotFive, Items.mainMenu()
                ) { guiClickEvent ->
                    run {
                        val inventoryItem = guiClickEvent.bukkitEvent.view.topInventory.getItem(13)

                        item = inventoryItem

                        openItemGUI(player, name, item)
                    }
                }
            }
        }

        player.openGUI(gui)
    }
}
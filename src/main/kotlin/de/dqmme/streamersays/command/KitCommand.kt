package de.dqmme.streamersays.command

import de.dqmme.streamersays.StreamerSays
import de.dqmme.streamersays.manager.KitManager
import de.dqmme.streamersays.manager.MessageManager
import de.dqmme.streamersays.misc.Emoji
import de.dqmme.streamersays.misc.Kit
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

class KitCommand(
    private val instance: StreamerSays,
    private val kitManager: KitManager,
    private val messageManager: MessageManager
) : TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("streamersays.kit") || !sender.hasPermission("streamersays.*")) {
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

                openKitGUI(sender, null, null)
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

    private fun openKitGUI(player: Player, kitName: String?, kitItems: List<ItemStack>?) {
        var name: String? = kitName
        var items: List<ItemStack>? = kitItems

        val gui = kSpigotGUI(GUIType.THREE_BY_NINE) {
            title = "§aKit erstellen"

            page(1) {
                placeholder(
                    Slots.All, Items.blackGlass()
                )

                pageChanger(
                    Slots.RowTwoSlotFour, itemStack(Material.NAME_TAG) {
                        meta {
                            name = "§aNamen festlegen " + if (name != null) "§a${Emoji.HOOK.string()}" else "§c${Emoji.X.string()}"

                            addLore {
                                +"§7Setze den §aNamen §7des Kits."
                                +"§aAktuell: §f${(name ?: "§7N/A")}"
                            }
                        }
                    },
                    2, null, null
                )

                pageChanger(
                    Slots.RowTwoSlotSix, itemStack(Material.ENDER_CHEST) {
                        meta {
                            name = "§aItems festlegen " + if (items != null && items!!.isNotEmpty()) "§a${Emoji.HOOK.string()}" else "§c${Emoji.X.string()}"

                            addLore {
                                +"§7Setze die §aItems §7des Kits."
                            }
                        }
                    },
                    3, null, null
                )

                if (name != null && items != null && items!!.isNotEmpty()) {
                    button(
                        Slots.RowOneSlotNine, itemStack(Material.LIME_DYE) {
                            meta {
                                name = "§aBestätigen"

                                addLore {
                                    +"§7Klicke dieses Item um §adas Kit §7zu speichern."
                                }
                            }
                        }
                    ) {
                        kitManager.addKit(Kit(name!!, items!!))

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
                            openKitGUI(player, name, items)
                        }
                        .text("Challenge-Name")
                        .itemLeft(
                            itemStack(Material.PAPER) {
                                meta {
                                    addLore {
                                        +"§7Trage §aden Namen §7des Kits ein."
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

                freeSlot(Slots.All)

                button(
                    Slots.RowOneSlotFive, itemStack(Material.GREEN_CONCRETE) {
                        meta {
                            name = "§aSpeichern und zum Hauptmenü"

                            addLore {
                                +"§7Klicke dieses Item um die Items §azu speichern."
                            }
                        }
                    }
                ) { guiClickEvent ->
                    run {
                        val contents = arrayListOf<ItemStack>()

                        for (content in guiClickEvent.bukkitEvent.view.topInventory.contents) {
                            contents.add(content)
                        }

                        contents.remove(
                            itemStack(Material.GREEN_CONCRETE) {
                                meta {
                                    name = "§aSpeichern und zum Hauptmenü"

                                    addLore {
                                        +"§7Klicke dieses Item um die Items §azu speichern."
                                    }
                                }
                            }
                        )

                        contents.removeIf { itemStack: ItemStack? -> itemStack == null }

                        items = contents

                        openKitGUI(player, name, items)
                    }
                }
            }
        }

        player.openGUI(gui)
    }
}
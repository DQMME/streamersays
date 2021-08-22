package de.dqmme.streamersays.command

import de.dqmme.streamersays.StreamerSays
import de.dqmme.streamersays.manager.KitManager
import de.dqmme.streamersays.manager.MessageManager
import de.dqmme.streamersays.misc.Challenge
import de.dqmme.streamersays.misc.Emojis
import de.dqmme.streamersays.misc.Kit
import de.dqmme.streamersays.util.ItemBuilder
import net.axay.kspigot.gui.*
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
                    Slots.All, ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                        .displayName("§c")
                        .build()
                )

                pageChanger(
                    Slots.RowTwoSlotFour, ItemBuilder(Material.NAME_TAG)
                        .displayName("§aNamen festlegen " + if (name != null) "§a${Emojis.HOOK}" else "§c${Emojis.X}")
                        .addLore("§7Setze den §aNamen §7des Kits.")
                        .addLore("§aAktuell: §f" + (name ?: "§7N/A"))
                        .build(),
                    2, null, null
                )

                pageChanger(
                    Slots.RowTwoSlotSix, ItemBuilder(Material.ENDER_CHEST)
                        .displayName("§aItems festlegen " + if (items != null && items!!.isNotEmpty()) "§a${Emojis.HOOK}" else "§c${Emojis.X}")
                        .addLore("§7Setze die §aItems §7des Kits.")
                        .build(),
                    3, null, null
                )

                if (name != null && items != null && items!!.isNotEmpty()) {
                    button(
                        Slots.RowOneSlotNine, ItemBuilder(Material.LIME_DYE)
                            .displayName("§aBestätigen")
                            .addLore("§7Klicke dieses Item um §adas Kit §7zu speichern.")
                            .build()
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
                    Slots.All, ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                        .displayName("§c")
                        .build()
                )

                button(
                    Slots.RowTwoSlotFive,
                    ItemBuilder(Material.GREEN_CONCRETE)
                        .displayName("§aKlick mich")
                        .addLore("§aKlick §7dieses Item um den Namen einzugeben.")
                        .build()
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
                            ItemBuilder(Material.PAPER)
                                .addLore("§7Trage §aden Namen §7des Kits ein.")
                                .build()
                        )
                        .title("§aNamen eingeben")
                        .plugin(instance)
                        .open(player)
                }

                pageChanger(
                    Slots.RowOneSlotOne, ItemBuilder(Material.PAPER)
                        .displayName("§aZurürck zum Hauptmenü")
                        .addLore("§7Kehre zurürck zum §aHauptmenü§7.")
                        .build(),
                    1, null, null
                )
            }

            page(3) {
                transitionFrom = PageChangeEffect.SLIDE_HORIZONTALLY
                transitionTo = PageChangeEffect.SLIDE_HORIZONTALLY

                freeSlot(Slots.All)

                button(
                    Slots.RowOneSlotFive, ItemBuilder(Material.GREEN_CONCRETE)
                        .displayName("§aSpeichern und zum Hauptmenü")
                        .addLore("§7Klicke dieses Item um die Items §azu speichern.")
                        .build()
                ) { guiClickEvent ->
                    run {
                        val contents = arrayListOf<ItemStack>()

                        for (content in guiClickEvent.bukkitEvent.view.topInventory.contents) {
                            contents.add(content)
                        }

                        contents.remove(
                            ItemBuilder(Material.GREEN_CONCRETE)
                                .displayName("§aSpeichern und zum Hauptmenü")
                                .addLore("§7Klicke dieses Item um die Items §azu speichern.")
                                .build()
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
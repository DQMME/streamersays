package de.dqmme.streamersays.command

import de.dqmme.streamersays.StreamerSays
import de.dqmme.streamersays.manager.ChallengeManager
import de.dqmme.streamersays.manager.MessageManager
import de.dqmme.streamersays.misc.Challenge
import de.dqmme.streamersays.misc.Emoji
import de.dqmme.streamersays.util.Items
import net.axay.kspigot.gui.*
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class ChallengeCommand(
    private var instance: StreamerSays,
    private var challengeManager: ChallengeManager,
    private var messageManager: MessageManager
) :
    TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("streamersays.challenge") || !sender.hasPermission("streamersays.*")) {
            sender.sendMessage(messageManager.message("no_permissions"))
            return false
        }

        if (args.isEmpty()) {
            sender.sendMessage(
                messageManager.message("invalid_usage")
                    .replace("%usage%", "/challenge <add>")
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
                            .replace("%usage%", "/challenge add")
                    )
                    return false
                }

                openSettingsGUI(sender)
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


    private fun openSettingsGUI(
        player: Player,
        challengeName: String? = null,
        challengeDescription: String? = null,
        challengeLocation: Location? = null,
        challengeGameMode: GameMode? = null
    ) {
        var name: String? = challengeName
        var description: String? = challengeDescription
        var location: Location? = challengeLocation
        var gameMode: GameMode? = challengeGameMode

        val gui = kSpigotGUI(GUIType.THREE_BY_NINE) {
            title = "§aChallenge hinzufügen"

            page(1) {
                placeholder(
                    Slots.All, Items.blackGlass()
                )

                pageChanger(
                    Slots.RowTwoSlotTwo, itemStack(Material.NAME_TAG) {
                        meta {
                            name =
                                "§aNamen festlegen " + if (name != null) "§a${Emoji.HOOK.string()}" else "§c${Emoji.X.string()}"

                            addLore {
                                +"§7Setze den §aNamen §7der Challenge."
                                +"§aAktuell: §f${if (name != null) name else "§7N/A"}"
                            }
                        }
                    },
                    2, null, null
                )

                pageChanger(
                    Slots.RowTwoSlotFour, itemStack(Material.OAK_SIGN) {
                        name =
                            "§aBeschreibung festlegen " + if (description != null) "§a${Emoji.HOOK.string()}" else "§c${Emoji.X.string()}"

                        meta {
                            addLore {
                                +"§7Setze die §aBeschreibung §7der Challenge."
                                +"§aAktuell: §f${if (description != null) description else "§7N/A"}"
                            }
                        }
                    },
                    3, null, null
                )

                pageChanger(
                    Slots.RowTwoSlotSix, itemStack(Material.ARROW) {
                        name =
                            "§aLocation festlegen " + if (location != null) "§a${Emoji.HOOK.string()}" else "§c${Emoji.X.string()}"

                        meta {
                            addLore {
                                +"§7Setze die §aLocation §7der Challenge."
                                +"§aAktuell: ${if (location != null) "§a${location!!.blockX}§7, §a${location!!.blockY}§7, §a${location!!.blockZ}" else "§7N/A"}"
                            }
                        }
                    },
                    4, null, null
                )

                pageChanger(
                    Slots.RowTwoSlotEight, itemStack(Material.GRASS_BLOCK) {
                        name =
                            "§aGameMode festlegen " + if (gameMode != null) "§a${Emoji.HOOK.string()}" else "§c${Emoji.X.string()}"

                        meta {
                            addLore {
                                +"§7Setze den §aGameMode §7der Challenge."
                                +"§aAktuell: §e${if (gameMode != null) gameMode!!.name else "§7N/A"}"
                            }
                        }
                    },
                    5, null, null
                )

                if (name != null && description != null && location != null && gameMode != null) {
                    button(
                        Slots.RowOneSlotNine, itemStack(Material.LIME_DYE) {
                            meta {
                                name = "§aBestätigen"

                                addLore {
                                    +"§7Klicke dieses Item um §adie Challenge §7zu speichern."
                                }
                            }
                        }
                    ) {
                        challengeManager.addChallenge(Challenge(name!!, description, location, gameMode))

                        player.closeInventory()

                        player.sendMessage(messageManager.message("challenge_created"))
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
                            openSettingsGUI(player, name, description, location, gameMode)
                        }
                        .text("Challenge-Name")
                        .itemLeft(
                            itemStack(Material.PAPER) {
                                meta {
                                    addLore {
                                        +"§7Trage §aden Namen §7der Challenge ein."
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

                placeholder(
                    Slots.All, Items.blackGlass()
                )

                button(
                    Slots.RowTwoSlotFive,
                    itemStack(Material.GREEN_CONCRETE) {
                        meta {
                            name = "§aKlick mich"

                            addLore {
                                +"§aKlick §7dieses Item um die Beschreibung einzugeben."
                            }
                        }
                    }
                ) {
                    AnvilGUI.Builder()
                        .preventClose()
                        .onComplete { _: Player, text: String? ->
                            description = text
                            AnvilGUI.Response.close()
                        }
                        .onClose {
                            openSettingsGUI(player, name, description, location, gameMode)
                        }
                        .text("Beschreibung")
                        .itemLeft(
                            itemStack(Material.PAPER) {
                                meta {
                                    addLore {
                                        +"§7Trage §adie Beschreibung §7der Challenge ein."
                                    }
                                }
                            }
                        )
                        .title("§aBeschreibung eingeben")
                        .plugin(instance)
                        .open(player)
                }

                pageChanger(
                    Slots.RowOneSlotOne, Items.mainMenu(),
                    1, null, null
                )
            }

            page(4) {
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
                                +"§aKlick §7dieses Item um die Location zu setzen."
                            }
                        }
                    }
                ) {
                    location = player.location
                    openSettingsGUI(player, name, description, location, gameMode)
                }

                pageChanger(
                    Slots.RowOneSlotOne, Items.mainMenu(),
                    1, null, null
                )
            }

            page(5) {
                transitionFrom = PageChangeEffect.SLIDE_HORIZONTALLY
                transitionTo = PageChangeEffect.SLIDE_HORIZONTALLY

                placeholder(
                    Slots.All, Items.blackGlass()
                )

                button(
                    Slots.RowTwoSlotTwo,
                    itemStack(Material.IRON_SWORD) {
                        meta {
                            name = "§aSurvival"

                            addLore {
                                +"§7Klicke dieses Item um den §aSpielmodus §7für die Challenge zu setzen."
                            }
                        }
                    }
                ) {
                    gameMode = GameMode.SURVIVAL
                    openSettingsGUI(player, name, description, location, gameMode)
                }

                button(
                    Slots.RowTwoSlotFour,
                    itemStack(Material.IRON_SWORD) {
                        meta {
                            name = "§aCreative"

                            addLore {
                                +"§7Klicke dieses Item um den §aSpielmodus §7für die Challenge zu setzen."
                            }
                        }
                    }
                ) {
                    gameMode = GameMode.CREATIVE
                    openSettingsGUI(player, name, description, location, gameMode)
                }

                button(
                    Slots.RowTwoSlotSix,
                    itemStack(Material.IRON_SWORD) {
                        meta {
                            name = "§aAdventure"

                            addLore {
                                +"§7Klicke dieses Item um den §aSpielmodus §7für die Challenge zu setzen."
                            }
                        }
                    }
                ) {
                    gameMode = GameMode.ADVENTURE
                    openSettingsGUI(player, name, description, location, gameMode)
                }

                button(
                    Slots.RowTwoSlotEight,
                    itemStack(Material.IRON_SWORD) {
                        meta {
                            name = "§aSpectator"

                            addLore {
                                +"§7Klicke dieses Item um den §aSpielmodus §7für die Challenge zu setzen."
                            }
                        }
                    }
                ) {
                    gameMode = GameMode.SPECTATOR
                    openSettingsGUI(player, name, description, location, gameMode)
                }

                pageChanger(
                    Slots.RowOneSlotOne, Items.mainMenu(),
                    1, null, null
                )
            }
        }
        player.openGUI(gui)
    }
}
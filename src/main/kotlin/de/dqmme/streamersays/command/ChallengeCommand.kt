package de.dqmme.streamersays.command

import de.dqmme.streamersays.StreamerSays
import de.dqmme.streamersays.manager.ChallengeManager
import de.dqmme.streamersays.manager.MessageManager
import de.dqmme.streamersays.misc.Challenge
import de.dqmme.streamersays.misc.Emojis
import de.dqmme.streamersays.util.ItemBuilder
import net.axay.kspigot.gui.*
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
                    Slots.All, ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                        .displayName("§c")
                        .build()
                )

                pageChanger(
                    Slots.RowTwoSlotTwo, ItemBuilder(Material.NAME_TAG)
                        .displayName("§aNamen festlegen " + if (name != null) "§a${Emojis.HOOK}" else "§c${Emojis.X}")
                        .addLore("§7Setze den §aNamen §7der Challenge.")
                        .addLore("§aAktuell: §f" + if (name != null) name else "§7N/A")
                        .build(),
                    2, null, null
                )

                pageChanger(
                    Slots.RowTwoSlotFour, ItemBuilder(Material.OAK_SIGN)
                        .displayName("§aBeschreibung festlegen " + if (description != null) "§a${Emojis.HOOK}" else "§c${Emojis.X}")
                        .addLore("§7Setze die §aBeschreibung §7der Challenge.")
                        .addLore("§aAktuell: §f" + if (description != null) description else "§7N/A")
                        .build(),
                    3, null, null
                )

                pageChanger(
                    Slots.RowTwoSlotSix, ItemBuilder(Material.ARROW)
                        .displayName("§aLocation festlegen " + if (location != null) "§a${Emojis.HOOK}" else "§c${Emojis.X}")
                        .addLore("§7Setze die §aLocation §7der Challenge.")
                        .addLore("§aAktuell: " + if (location != null) "§a${location!!.blockX}§7, §a${location!!.blockY}§7, §a${location!!.blockZ}" else "§7N/A")
                        .build(),
                    4, null, null
                )

                pageChanger(
                    Slots.RowTwoSlotEight, ItemBuilder(Material.GRASS_BLOCK)
                        .displayName("§aGameMode festlegen " + if (gameMode != null) "§a${Emojis.HOOK}" else "§c${Emojis.X}")
                        .addLore("§7Setze den §aGameMode §7der Challenge.")
                        .addLore("§aAktuell: §e" + if (gameMode != null) gameMode!!.name else "§7N/A")
                        .build(),
                    5, null, null
                )

                if (name != null && description != null && location != null && gameMode != null) {
                    button(
                        Slots.RowOneSlotNine, ItemBuilder(Material.LIME_DYE)
                            .displayName("§aBestätigen")
                            .addLore("§7Klicke dieses Item um §adie Challenge §7zu speichern.")
                            .build()
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
                            openSettingsGUI(player, name, description, location, gameMode)
                        }
                        .text("Challenge-Name")
                        .itemLeft(
                            ItemBuilder(Material.PAPER)
                                .addLore("§7Trage §aden Namen §7der Challenge ein.")
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

                placeholder(
                    Slots.All, ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                        .displayName("§c")
                        .build()
                )

                button(
                    Slots.RowTwoSlotFive,
                    ItemBuilder(Material.GREEN_CONCRETE)
                        .displayName("§aKlick mich")
                        .addLore("§aKlick §7dieses Item um die Beschreibung einzugeben.")
                        .build()
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
                            ItemBuilder(Material.PAPER)
                                .addLore("§7Trage §adie Beschreibung §7der Challenge ein.")
                                .build()
                        )
                        .title("§aBeschreibung eingeben")
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

            page(4) {
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
                        .addLore("§aKlick §7dieses Item um die Location zu setzen.")
                        .build()
                ) {
                    location = player.location
                    openSettingsGUI(player, name, description, location, gameMode)
                }

                pageChanger(
                    Slots.RowOneSlotOne, ItemBuilder(Material.PAPER)
                        .displayName("§aZurürck zum Hauptmenü")
                        .addLore("§7Kehre zurürck zum §aHauptmenü§7.")
                        .build(),
                    1, null, null
                )
            }

            page(5) {
                transitionFrom = PageChangeEffect.SLIDE_HORIZONTALLY
                transitionTo = PageChangeEffect.SLIDE_HORIZONTALLY

                placeholder(
                    Slots.All, ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                        .displayName("§c")
                        .build()
                )

                button(
                    Slots.RowTwoSlotTwo,
                    ItemBuilder(Material.IRON_SWORD)
                        .displayName("§aSurvival")
                        .addLore("§7Klicke dieses Item um den §aSpielmodus §7für die Challenge zu setzen.")
                        .build()
                ) {
                    gameMode = GameMode.SURVIVAL
                    openSettingsGUI(player, name, description, location, gameMode)
                }

                button(
                    Slots.RowTwoSlotFour,
                    ItemBuilder(Material.GRASS_BLOCK)
                        .displayName("§aCreative")
                        .addLore("§7Klicke dieses Item um den §aSpielmodus §7für die Challenge zu setzen.")
                        .build()
                ) {
                    gameMode = GameMode.CREATIVE
                    openSettingsGUI(player, name, description, location, gameMode)
                }

                button(
                    Slots.RowTwoSlotSix,
                    ItemBuilder(Material.MAP)
                        .displayName("§aAdventure")
                        .addLore("§7Klicke dieses Item um den §aSpielmodus §7für die Challenge zu setzen.")
                        .build()
                ) {
                    gameMode = GameMode.ADVENTURE
                    openSettingsGUI(player, name, description, location, gameMode)
                }

                button(
                    Slots.RowTwoSlotEight,
                    ItemBuilder(Material.ENDER_EYE)
                        .displayName("§aSpectator")
                        .addLore("§7Klicke dieses Item um den §aSpielmodus §7für die Challenge zu setzen.")
                        .build()
                ) {
                    gameMode = GameMode.SPECTATOR
                    openSettingsGUI(player, name, description, location, gameMode)
                }

                pageChanger(
                    Slots.RowOneSlotOne, ItemBuilder(Material.PAPER)
                        .displayName("§aZurürck zum Hauptmenü")
                        .addLore("§7Kehre zurürck zum §aHauptmenü§7.")
                        .build(),
                    1, null, null
                )
            }
        }
        player.openGUI(gui)
    }
}
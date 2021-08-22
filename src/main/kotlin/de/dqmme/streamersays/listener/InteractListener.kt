package de.dqmme.streamersays.listener

import de.dqmme.streamersays.manager.ChallengeManager
import de.dqmme.streamersays.manager.GameManager
import de.dqmme.streamersays.manager.KitManager
import de.dqmme.streamersays.manager.MessageManager
import de.dqmme.streamersays.misc.Kit
import de.dqmme.streamersays.misc.StreamerSaysPlayer
import de.dqmme.streamersays.util.ItemBuilder
import de.dqmme.streamersays.util.Items
import de.dqmme.streamersays.util.SkullBuilder
import net.axay.kspigot.gui.*
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class InteractListener(
    private val gameManager: GameManager,
    private val challengeManager: ChallengeManager,
    private val kitManager: KitManager,
    private val messageManager: MessageManager
) :
    Listener {
    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val item = event.item ?: return

        //if (event.action != Action.RIGHT_CLICK_BLOCK || event.action != Action.RIGHT_CLICK_AIR) return

        if (item.isSimilar(Items.startItem())) {
            gameManager.running = true
            event.player.inventory.setItem(event.player.inventory.heldItemSlot, ItemStack(Material.AIR))
            event.player.inventory.setItem(4, Items.menuItem())
        } else if (item.isSimilar(Items.menuItem())) {
            event.player.openGUI(settingsGUI())
        }
    }

    private fun settingsGUI(): GUI<ForInventoryThreeByNine> {
        return kSpigotGUI(GUIType.THREE_BY_NINE) {
            title = "§aEinstellungen"

            page(1) {
                transitionFrom = PageChangeEffect.SLIDE_HORIZONTALLY
                transitionTo = PageChangeEffect.SLIDE_HORIZONTALLY

                placeholder(
                    Slots.All, ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                        .displayName("§c")
                        .build()
                )

                pageChanger(
                    Slots.RowTwoSlotThree, ItemBuilder(Material.DIAMOND_SWORD)
                        .displayName("§bKits")
                        .addLore("§7Klicke das Item um das §bKits-Menü §7zu öffnen.")
                        .build(), 2, null, null
                )

                pageChanger(
                    Slots.RowTwoSlotFive, ItemBuilder(Material.TRIDENT)
                        .displayName("§3Challenges")
                        .addLore("§7Klicke das Item um das §3Challenges-Menü §7zu öffnen.")
                        .build(), 3, null, null
                )

                pageChanger(
                    Slots.RowTwoSlotSeven, ItemBuilder(Material.IRON_SWORD)
                        .displayName("§fItems")
                        .addLore("§7Klicke das Item um das §fItem-Menü §7zu öffnen.")
                        .build(), 4, null, null
                )
            }

            page(2) {
                transitionFrom = PageChangeEffect.SLIDE_HORIZONTALLY
                transitionTo = PageChangeEffect.SLIDE_HORIZONTALLY

                val compound = createRectCompound<ItemStack>(
                    Slots.RowOneSlotOne, Slots.RowThreeSlotEight,
                    iconGenerator = {
                        ItemStack(it)
                    },
                    onClick = { clickEvent, element ->
                        val kitName = element.itemMeta!!.displayName.replace("§a", "")
                        val kit = kitManager.kitByName(kitName)

                        clickEvent.player.openGUI(kitGiveGUI(kit!!))
                    }
                )

                for (kit in kitManager.kits()) {
                    compound.addContent(
                        ItemBuilder(Material.DIAMOND_SWORD)
                            .displayName("§a${kit.name}")
                            .build()
                    )
                }

                compound.sortContentBy { it.itemMeta!!.displayName }
                compoundScroll(
                    Slots.RowOneSlotNine,
                    ItemStack(Material.PAPER), compound, scrollTimes = 6
                )

                compoundScroll(
                    Slots.RowThreeSlotNine,
                    ItemStack(Material.PAPER), compound, scrollTimes = 6, reverse = true
                )
            }

            page(3) {
                val compound = createRectCompound<ItemStack>(
                    Slots.RowOneSlotOne, Slots.RowThreeSlotEight,
                    iconGenerator = {
                        ItemStack(it)
                    },
                    onClick = { _, element ->
                        val challengeName = element.itemMeta!!.displayName.replace("§a", "")
                        val challenge = challengeManager.challengeByName(challengeName)

                        gameManager.currentChallenge = challenge

                        for (all in Bukkit.getOnlinePlayers()) {
                            val streamerSaysPlayer = StreamerSaysPlayer.getPlayer(all)

                            if (streamerSaysPlayer.isAlive) {
                                all.teleport(challenge!!.location!!)
                                if (gameManager.gameMaster != all) {
                                    all.gameMode = challenge.gameMode!!
                                }
                            }
                        }

                        Bukkit.broadcastMessage(
                            messageManager.message("challenge_started")
                                .replace("%challenge%", challengeName)
                        )
                    }
                )

                for (challenge in challengeManager.challenges()) {
                    compound.addContent(
                        ItemBuilder(Material.DIAMOND_SWORD)
                            .displayName("§a${challenge.name}")
                            .addLore("§aBeschreibung:")
                            .addLore("§f${challenge.description!!}")
                            .addLore("§aLocation:")
                            .addLore("§a${challenge.location!!.blockX}§7, §a${challenge.location.blockY}§7, §a${challenge.location.blockZ}")
                            .addLore("§aGameMode:")
                            .addLore("§e${challenge.gameMode!!.name}")
                            .build()
                    )
                }

                compound.sortContentBy { it.itemMeta!!.displayName }
                compoundScroll(
                    Slots.RowOneSlotNine,
                    ItemStack(Material.PAPER), compound, scrollTimes = 6
                )

                compoundScroll(
                    Slots.RowThreeSlotNine,
                    ItemStack(Material.PAPER), compound, scrollTimes = 6, reverse = true
                )
            }
        }
    }

    private fun kitGiveGUI(kit: Kit): GUI<ForInventorySixByNine> {
        return kSpigotGUI(GUIType.SIX_BY_NINE) {
            title = "§aKit geben"
            page(1) {
                val compound = createRectCompound<ItemStack>(
                    Slots.RowOneSlotOne, Slots.RowThreeSlotEight,
                    iconGenerator = {
                        ItemStack(it)
                    },
                    onClick = { _, element ->
                        val playerName = element.itemMeta!!.displayName.replace("§e", "")

                        if (playerName.lowercase() == "alle spieler") {
                            for (all in Bukkit.getOnlinePlayers()) {
                                for (item in kit.items) {
                                    all.inventory.addItem(item)
                                }
                            }
                        } else {
                            val player = Bukkit.getPlayer(playerName)

                            if (player != null) {
                                for (item in kit.items) {
                                    player.inventory.addItem(item)
                                }
                            }
                        }
                    }
                )

                compound.addContent(
                    ItemBuilder(Material.LIME_CONCRETE)
                        .displayName("§eAlle Spieler")
                        .addLore("§7Gebe §e§lallen Spielern §b${kit.name}§7.")
                        .build()
                )

                for (all in Bukkit.getOnlinePlayers()) {
                    compound.addContent(
                        SkullBuilder()
                            .displayName("§e${all.name}")
                            .addLore("§7Gebe §e${all.name} §b${kit.name}§7.")
                            .owner(all)
                            .build()
                    )
                }

                compound.sortContentBy { it.itemMeta!!.displayName }
                compoundScroll(
                    Slots.RowOneSlotNine,
                    ItemStack(Material.PAPER), compound, scrollTimes = 6
                )

                compoundScroll(
                    Slots.RowSixSlotNine,
                    ItemStack(Material.PAPER), compound, scrollTimes = 6, reverse = true
                )
            }
        }
    }
}
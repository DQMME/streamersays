package de.dqmme.streamersays.listener

import de.dqmme.streamersays.manager.*
import de.dqmme.streamersays.misc.Item
import de.dqmme.streamersays.misc.Kit
import de.dqmme.streamersays.misc.StreamerSaysPlayer
import de.dqmme.streamersays.util.Items
import de.dqmme.streamersays.util.SkullBuilder
import net.axay.kspigot.gui.*
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
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
    private val itemManager: ItemManager,
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
                    Slots.All, Items.blackGlass()
                )

                pageChanger(
                    Slots.RowTwoSlotThree, itemStack(Material.DIAMOND_SWORD) {
                        meta {
                            name = "§bKits"

                            addLore {
                                +"§7Klicke das Item um das §bKits-Menü §7zu öffnen."
                            }
                        }
                    }, 2, null, null
                )

                pageChanger(
                    Slots.RowTwoSlotFive, itemStack(Material.TRIDENT) {
                        meta {
                            name = "§3Challenges"

                            addLore {
                                +"§7Klicke das item um das §bChallenge-Menü §7zu öffnen."
                            }
                        }
                    }, 3, null, null
                )

                pageChanger(
                    Slots.RowTwoSlotSeven, itemStack(Material.IRON_SWORD) {
                        meta {
                            name = "§fItems"

                            addLore {
                                +"§7Klicke das Item um das §fItem-Menü §7zu öffnen."
                            }
                        }
                    }, 4, null, null
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
                        itemStack(Material.DIAMOND_SWORD) {
                            meta {
                                name = "§a${kit.name}"

                                addLore {
                                    +"§aItems:"
                                    +"§7- §e${kit.items[0].itemMeta!!.displayName}"
                                    +"§7- §e${kit.items[1].itemMeta!!.displayName}"
                                    +"§7- §e${kit.items[2].itemMeta!!.displayName}"
                                    +"§7- §e${kit.items[3].itemMeta!!.displayName}"
                                    +"§7- §e${kit.items[4].itemMeta!!.displayName}"
                                }
                            }
                        }
                    )
                }

                compound.sortContentBy { it.itemMeta!!.displayName }

                compoundScroll(
                    Slots.RowOneSlotNine,
                    Items.scrollDown(), compound, scrollTimes = 6
                )

                compoundScroll(
                    Slots.RowThreeSlotNine,
                    Items.scrollUp(), compound, scrollTimes = 6, reverse = true
                )
            }

            page(3) {
                val compound = createRectCompound<ItemStack>(
                    Slots.RowOneSlotOne, Slots.RowThreeSlotEight,
                    iconGenerator = {
                        ItemStack(it)
                    },
                    onClick = { clickEvent, element ->
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

                        clickEvent.bukkitEvent.isCancelled = true
                    }
                )

                for (challenge in challengeManager.challenges()) {
                    compound.addContent(
                        itemStack(Material.TRIDENT) {
                            meta {
                                name = "§a${challenge.name}"

                                addLore {
                                    +"§aBeschreibung:"
                                    +"§f${challenge.description!!}"
                                    +"§aLocation:"
                                    +"§a${challenge.location!!.blockX}§7, §a${challenge.location.blockY}§7, §a${challenge.location.blockZ}"
                                    +"§aGameMode:"
                                    +"§e${challenge.gameMode!!.name}"
                                }
                            }
                        }
                    )
                }

                compound.sortContentBy { it.itemMeta!!.displayName }

                compoundScroll(
                    Slots.RowOneSlotNine,
                    Items.scrollDown(), compound, scrollTimes = 6
                )

                compoundScroll(
                    Slots.RowThreeSlotNine,
                    Items.scrollUp(), compound, scrollTimes = 6, reverse = true
                )
            }

            page(4) {
                transitionFrom = PageChangeEffect.SLIDE_HORIZONTALLY
                transitionTo = PageChangeEffect.SLIDE_HORIZONTALLY

                val compound = createRectCompound<ItemStack>(
                    Slots.RowOneSlotOne, Slots.RowThreeSlotEight,
                    iconGenerator = {
                        ItemStack(it)
                    },
                    onClick = { clickEvent, element ->
                        val itemName = element.itemMeta!!.displayName.replace("§a", "")
                        val item = itemManager.itemByName(itemName)

                        clickEvent.player.openGUI(itemGiveGUI(item!!))
                    }
                )

                for (item in itemManager.items()) {
                    compound.addContent(
                        itemStack(Material.IRON_SWORD) {
                            meta {
                                name = "§a${item.name}"

                                addLore {
                                    +"§aDisplayName"
                                    +"§e${item.itemStack.itemMeta!!.displayName}"
                                    +"§aMaterial:"
                                    +"§e${item.itemStack.type}"
                                }
                            }
                        }
                    )
                }

                compound.sortContentBy { it.itemMeta!!.displayName }

                compoundScroll(
                    Slots.RowOneSlotNine,
                    Items.scrollDown(), compound, scrollTimes = 6
                )

                compoundScroll(
                    Slots.RowThreeSlotNine,
                    Items.scrollUp(), compound, scrollTimes = 6, reverse = true
                )
            }
        }
    }

    private fun kitGiveGUI(kit: Kit): GUI<ForInventoryThreeByNine> {
        return kSpigotGUI(GUIType.THREE_BY_NINE) {
            title = "§aKit geben"
            page(1) {
                val compound = createRectCompound<ItemStack>(
                    Slots.RowOneSlotOne, Slots.RowThreeSlotEight,
                    iconGenerator = {
                        ItemStack(it)
                    },
                    onClick = { event, element ->
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

                        event.bukkitEvent.isCancelled = true
                    }
                )

                compound.addContent(
                    itemStack(Material.LIME_CONCRETE) {
                        meta {
                            name = "§eAlle Spieler"

                            addLore {
                                +"§7Gebe §e§lallen Spielern §b${kit.name}§7."
                            }
                        }
                    }
                )

                for (all in Bukkit.getOnlinePlayers()) {
                    val streamerSaysPlayer = StreamerSaysPlayer.getPlayer(all)

                    if (streamerSaysPlayer.isAlive) {
                        compound.addContent(
                            SkullBuilder()
                                .displayName("§e${all.name}")
                                .addLore("§7Gebe §e${all.name} §b${kit.name}§7.")
                                .owner(all)
                                .build()
                        )
                    }
                }

                compound.sortContentBy { it.itemMeta!!.displayName }

                compoundScroll(
                    Slots.RowOneSlotNine,
                    Items.scrollDown(), compound, scrollTimes = 6
                )

                compoundScroll(
                    Slots.RowThreeSlotNine,
                    Items.scrollUp(), compound, scrollTimes = 6, reverse = true
                )
            }
        }
    }

    private fun itemGiveGUI(item: Item): GUI<ForInventoryThreeByNine> {
        return kSpigotGUI(GUIType.THREE_BY_NINE) {
            title = "§aItem geben"
            page(1) {
                val compound = createRectCompound<ItemStack>(
                    Slots.RowOneSlotOne, Slots.RowThreeSlotEight,
                    iconGenerator = {
                        ItemStack(it)
                    },
                    onClick = { event, element ->
                        val playerName = element.itemMeta!!.displayName.replace("§e", "")

                        if (playerName.lowercase() == "alle spieler") {
                            for (all in Bukkit.getOnlinePlayers()) {
                                all.inventory.addItem(item.itemStack)
                            }
                        } else {
                            val player = Bukkit.getPlayer(playerName)

                            player?.inventory?.addItem(item.itemStack)
                        }

                        event.bukkitEvent.isCancelled = true
                    }
                )

                compound.addContent(
                    itemStack(Material.LIME_CONCRETE) {
                        meta {
                            name = "§eAlle Spieler"

                            addLore {
                                +"§7Gebe §e§lallen Spielern §b${item.name}§7."
                            }
                        }
                    }
                )

                for (all in Bukkit.getOnlinePlayers()) {
                    val streamerSaysPlayer = StreamerSaysPlayer.getPlayer(all)

                    if (streamerSaysPlayer.isAlive) {
                        compound.addContent(
                            SkullBuilder()
                                .displayName("§e${all.name}")
                                .addLore("§7Gebe §e${all.name} §b${item.name}§7.")
                                .owner(all)
                                .build()
                        )
                    }
                }

                compound.sortContentBy { it.itemMeta!!.displayName }

                compoundScroll(
                    Slots.RowOneSlotNine,
                    Items.scrollDown(), compound, scrollTimes = 6
                )

                compoundScroll(
                    Slots.RowThreeSlotNine,
                    Items.scrollUp(), compound, scrollTimes = 6, reverse = true
                )
            }
        }
    }
}
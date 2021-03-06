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
            title = "┬žaEinstellungen"

            page(1) {
                transitionFrom = PageChangeEffect.SLIDE_HORIZONTALLY
                transitionTo = PageChangeEffect.SLIDE_HORIZONTALLY

                placeholder(
                    Slots.All, Items.blackGlass()
                )

                pageChanger(
                    Slots.RowTwoSlotThree, itemStack(Material.DIAMOND_SWORD) {
                        meta {
                            name = "┬žbKits"

                            addLore {
                                +"┬ž7Klicke das Item um das ┬žbKits-Men├╝ ┬ž7zu ├Âffnen."
                            }
                        }
                    }, 2, null, null
                )

                pageChanger(
                    Slots.RowTwoSlotFive, itemStack(Material.TRIDENT) {
                        meta {
                            name = "┬ž3Challenges"

                            addLore {
                                +"┬ž7Klicke das item um das ┬žbChallenge-Men├╝ ┬ž7zu ├Âffnen."
                            }
                        }
                    }, 3, null, null
                )

                pageChanger(
                    Slots.RowTwoSlotSeven, itemStack(Material.IRON_SWORD) {
                        meta {
                            name = "┬žfItems"

                            addLore {
                                +"┬ž7Klicke das Item um das ┬žfItem-Men├╝ ┬ž7zu ├Âffnen."
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
                        val kitName = element.itemMeta!!.displayName.replace("┬ža", "")
                        val kit = kitManager.kitByName(kitName)

                        clickEvent.player.openGUI(kitGiveGUI(kit!!))
                    }
                )

                for (kit in kitManager.kits()) {
                    compound.addContent(
                        itemStack(Material.DIAMOND_SWORD) {
                            meta {
                                name = "┬ža${kit.name}"

                                addLore {
                                    +"┬žaItems:"
                                    +"┬ž7- ┬že${kit.items[0].itemMeta!!.displayName}"
                                    +"┬ž7- ┬že${kit.items[1].itemMeta!!.displayName}"
                                    +"┬ž7- ┬že${kit.items[2].itemMeta!!.displayName}"
                                    +"┬ž7- ┬že${kit.items[3].itemMeta!!.displayName}"
                                    +"┬ž7- ┬že${kit.items[4].itemMeta!!.displayName}"
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
                        val challengeName = element.itemMeta!!.displayName.replace("┬ža", "")
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
                                name = "┬ža${challenge.name}"

                                addLore {
                                    +"┬žaBeschreibung:"
                                    +"┬žf${challenge.description!!}"
                                    +"┬žaLocation:"
                                    +"┬ža${challenge.location!!.blockX}┬ž7, ┬ža${challenge.location.blockY}┬ž7, ┬ža${challenge.location.blockZ}"
                                    +"┬žaGameMode:"
                                    +"┬že${challenge.gameMode!!.name}"
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
                        val itemName = element.itemMeta!!.displayName.replace("┬ža", "")
                        val item = itemManager.itemByName(itemName)

                        clickEvent.player.openGUI(itemGiveGUI(item!!))
                    }
                )

                for (item in itemManager.items()) {
                    compound.addContent(
                        itemStack(Material.IRON_SWORD) {
                            meta {
                                name = "┬ža${item.name}"

                                addLore {
                                    +"┬žaDisplayName"
                                    +"┬že${item.itemStack.itemMeta!!.displayName}"
                                    +"┬žaMaterial:"
                                    +"┬že${item.itemStack.type}"
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
            title = "┬žaKit geben"
            page(1) {
                val compound = createRectCompound<ItemStack>(
                    Slots.RowOneSlotOne, Slots.RowThreeSlotEight,
                    iconGenerator = {
                        ItemStack(it)
                    },
                    onClick = { event, element ->
                        val playerName = element.itemMeta!!.displayName.replace("┬že", "")

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
                            name = "┬žeAlle Spieler"

                            addLore {
                                +"┬ž7Gebe ┬že┬žlallen Spielern ┬žb${kit.name}┬ž7."
                            }
                        }
                    }
                )

                for (all in Bukkit.getOnlinePlayers()) {
                    val streamerSaysPlayer = StreamerSaysPlayer.getPlayer(all)

                    if (streamerSaysPlayer.isAlive) {
                        compound.addContent(
                            SkullBuilder()
                                .displayName("┬že${all.name}")
                                .addLore("┬ž7Gebe ┬že${all.name} ┬žb${kit.name}┬ž7.")
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
            title = "┬žaItem geben"
            page(1) {
                val compound = createRectCompound<ItemStack>(
                    Slots.RowOneSlotOne, Slots.RowThreeSlotEight,
                    iconGenerator = {
                        ItemStack(it)
                    },
                    onClick = { event, element ->
                        val playerName = element.itemMeta!!.displayName.replace("┬že", "")

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
                            name = "┬žeAlle Spieler"

                            addLore {
                                +"┬ž7Gebe ┬že┬žlallen Spielern ┬žb${item.name}┬ž7."
                            }
                        }
                    }
                )

                for (all in Bukkit.getOnlinePlayers()) {
                    val streamerSaysPlayer = StreamerSaysPlayer.getPlayer(all)

                    if (streamerSaysPlayer.isAlive) {
                        compound.addContent(
                            SkullBuilder()
                                .displayName("┬že${all.name}")
                                .addLore("┬ž7Gebe ┬že${all.name} ┬žb${item.name}┬ž7.")
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
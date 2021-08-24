package de.dqmme.streamersays.util

import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class Items {
    companion object {
        fun blackGlass() : ItemStack {
            return itemStack(Material.BLACK_STAINED_GLASS_PANE) {
                meta {
                    name = "§c"
                }
            }
        }

        fun mainMenu() : ItemStack {
            return itemStack(Material.PAPER) {
                meta {
                    name = "§aZurürck zum Hauptmenü"

                    addLore {
                        +"§7Kehre zurürck zum §aHauptmenü§7."
                    }
                }
            }
        }

        fun scrollUp() : ItemStack {
            return itemStack(Material.PAPER) {
                meta {
                    name = "§fNach oben"

                    addLore {
                        +"§7Scrolle §fnach oben§7."
                    }
                }
            }
        }

        fun scrollDown() : ItemStack {
            return itemStack(Material.PAPER) {
                meta {
                    name = "§fNach unten"

                    addLore {
                        +"§7Scrolle §fnach unten§7."
                    }
                }
            }
        }

        fun startItem(): ItemStack {
            return itemStack(Material.LIME_DYE) {
                meta {
                    name = "§aSpiel starten"
                    addLore {
                        +"§7Rechtsklicke das Item um das Spiel zu §astarten§7."
                    }
                }
            }
        }

        fun menuItem(): ItemStack {
            return itemStack(Material.LIME_DYE) {
                meta {
                    name = "§fEinstellungen"
                    addLore {
                        +"§7Rechtsklicke das Item um §fdie Einstellungen §7zu öffnen."
                    }
                }
            }
        }
    }
}
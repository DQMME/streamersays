package de.dqmme.streamersays.util

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class Items {
    companion object {
        fun startItem(): ItemStack {
            return ItemBuilder(Material.LIME_DYE)
                .displayName("§aSpiel starten")
                .addLore("§7Rechtsklicke das Item um das Spiel zu §astarten§7.")
                .build()
        }

        fun menuItem(): ItemStack {
            return ItemBuilder(Material.NETHER_STAR)
                .displayName("§fEinstellungen")
                .addLore("§7Rechtsklicke das Item um §fdie Einstellungen §7zu öffnen.")
                .build()
        }
    }
}
package de.dqmme.streamersays.util

import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta

class SkullBuilder {
    private var itemStack = ItemStack(Material.PLAYER_HEAD)
    private var itemMeta: SkullMeta = itemStack.itemMeta!! as SkullMeta

    fun displayName(displayName: String): SkullBuilder {
        itemMeta.setDisplayName(displayName)
        return this
    }

    fun addLore(lore: String): SkullBuilder {
        val loreList = arrayListOf<String>()

        if (itemMeta.hasLore()) {
            for (string in itemMeta.lore!!) {
                loreList.add(string)
            }
        }

        loreList.add(lore)

        itemMeta.lore = loreList
        return this
    }

    fun owner(owner: OfflinePlayer): SkullBuilder {
        itemMeta.owningPlayer = owner
        return this
    }

    fun build(): ItemStack {
        itemStack.itemMeta = itemMeta
        return itemStack
    }
}
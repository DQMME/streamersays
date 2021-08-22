package de.dqmme.streamersays.util

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class ItemBuilder(material: Material) {
    private var itemStack = ItemStack(material)
    private var itemMeta: ItemMeta = itemStack.itemMeta!!

    fun displayName(displayName: String): ItemBuilder {
        itemMeta.setDisplayName(displayName)
        return this
    }

    fun addLore(lore: String): ItemBuilder {
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

    fun build(): ItemStack {
        itemStack.itemMeta = itemMeta
        return itemStack
    }
}
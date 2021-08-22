package de.dqmme.streamersays.manager

import de.dqmme.streamersays.StreamerSays
import de.dqmme.streamersays.misc.Item
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack

class ItemManager(private val instance: StreamerSays) {
    private var itemConf: YamlConfiguration

    init {
        itemConf = YamlConfiguration.loadConfiguration(instance.itemFile)
    }

    fun addItem(item: Item) {
        itemConf.set(item.name, item.itemStack)
        saveFile()
    }

    fun itemByName(name: String): Item? {
        for (item in items()) {
            if (item.name.lowercase() == name.lowercase()) {
                return item
            }
        }
        return null
    }

    fun items(): List<Item> {
        val itemList = arrayListOf<Item>()

        for (key in itemConf.getKeys(false)) {
            val itemStack = itemConf.getItemStack(key)

            itemList.add(Item(key, itemStack!!))
        }

        return itemList
    }

    fun saveFile() {
        itemConf.save(instance.itemFile)
    }

    fun reloadFile() {
        itemConf = YamlConfiguration.loadConfiguration(instance.itemFile)
    }
}
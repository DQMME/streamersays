package de.dqmme.streamersays.manager

import de.dqmme.streamersays.StreamerSays
import de.dqmme.streamersays.misc.Kit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack

class KitManager(private val instance: StreamerSays) {
    private var kitConf: YamlConfiguration

    init {
        kitConf = YamlConfiguration.loadConfiguration(instance.kitFile)
    }

    fun addKit(kit: Kit) {
        kitConf.set(kit.name.lowercase(), kit.items)
        saveFile()
    }

    fun kitByName(name: String): Kit? {
        for (kit in kits()) {
            if (kit.name == name.lowercase()) {
                return kit
            }
        }
        return null
    }

    fun kits(): List<Kit> {
        val kitList = arrayListOf<Kit>()

        for (key in kitConf.getKeys(false)) {
            val list = kitConf.getList(key) as List<ItemStack>

            kitList.add(Kit(key, list))
        }
        return kitList
    }

    fun saveFile() {
        kitConf.save(instance.kitFile)
    }

    fun reloadFile() {
        kitConf = YamlConfiguration.loadConfiguration(instance.kitFile)
    }
}
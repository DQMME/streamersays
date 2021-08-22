package de.dqmme.streamersays.manager

import de.dqmme.streamersays.StreamerSays
import org.bukkit.ChatColor
import org.bukkit.configuration.file.YamlConfiguration

class MessageManager(private val instance: StreamerSays) {
    private var messageConf: YamlConfiguration

    init {
        messageConf = YamlConfiguration.loadConfiguration(instance.messageFile)
    }

    fun message(name: String): String {
        var message: String? = messageConf.getString(name.lowercase()) ?: return "Nachricht $name nicht gefunden."

        val prefix = messageConf.getString("prefix")

        message = ChatColor.translateAlternateColorCodes('&', message!!)
            .replace("%prefix%", ChatColor.translateAlternateColorCodes('&', prefix!!))

        return message
    }

    fun saveFile() {
        messageConf.save(instance.messageFile)
    }

    fun reloadFile() {
        messageConf = YamlConfiguration.loadConfiguration(instance.messageFile)
    }
}
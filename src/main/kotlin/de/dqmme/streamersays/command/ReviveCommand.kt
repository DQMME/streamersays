package de.dqmme.streamersays.command

import de.dqmme.streamersays.manager.GameManager
import de.dqmme.streamersays.manager.MessageManager
import de.dqmme.streamersays.misc.StreamerSaysPlayer
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class ReviveCommand(private val gameManager: GameManager, private val messageManager: MessageManager) : TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("streamersays.revive") || !sender.hasPermission("streamersays.*")) {
            sender.sendMessage(messageManager.message("no_permissions"))
            return false
        }

        if (args.size > 2) {
            sender.sendMessage(
                messageManager.message("invalid_usage")
                    .replace("%usage%", "/revive <player>")
            )
            return false
        }

        if (args.isEmpty()) {
            if(sender !is Player) {
                sender.sendMessage(messageManager.message("not_a_player"))
                return false
            }

            revivePlayer(sender, sender)
        } else {
            val player = Bukkit.getPlayer(args[0])

            if(player == null) {
                sender.sendMessage(messageManager.message("player_not_found"))
                return false
            }

            revivePlayer(player)

            sender.sendMessage(messageManager.message("player_revived")
                .replace("%player%", player.name))
        }

        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String>? {
        return if(args.size == 1) {
            null
        } else{
            arrayListOf("")
        }
    }

    private fun revivePlayer(player: Player, sender: Player? = null) {
        val streamerSaysPlayer = StreamerSaysPlayer.getPlayer(player)

        val currentChallenge = gameManager.currentChallenge

        var gameMode = GameMode.SURVIVAL

        if (currentChallenge?.gameMode != null) {
            gameMode = currentChallenge.gameMode
        }

        streamerSaysPlayer.isAlive = true
        player.gameMode = gameMode

        if(sender != null) {
            player.teleport(sender.location)
            sender.sendMessage(messageManager.message("player_revived")
                .replace("%player%", player.name))
        }

        player.sendMessage(messageManager.message("you_got_revived"))
    }
}
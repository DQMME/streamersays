package de.dqmme.streamersays.misc

import org.bukkit.entity.Player

class StreamerSaysPlayer(val player: Player) {
    var isAlive: Boolean = false

    companion object {
        private val players = hashMapOf<Player, StreamerSaysPlayer>()

        fun getPlayer(player: Player): StreamerSaysPlayer {
            return if(players[player] != null) {
                players[player]!!
            } else{
                val streamerSaysPlayer = StreamerSaysPlayer(player)

                players[player] = streamerSaysPlayer

                streamerSaysPlayer
            }
        }
    }
}
package de.dqmme.streamersays.misc

import org.bukkit.GameMode
import org.bukkit.Location

data class Challenge(val name: String, val description: String?, val location: Location?, val gameMode: GameMode?)

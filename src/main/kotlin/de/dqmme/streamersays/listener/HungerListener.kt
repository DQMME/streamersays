package de.dqmme.streamersays.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.FoodLevelChangeEvent

class HungerListener : Listener {
    @EventHandler
    fun onFoodLevelChange(event: FoodLevelChangeEvent) {
        event.entity.foodLevel = 20
        event.isCancelled = true
    }
}
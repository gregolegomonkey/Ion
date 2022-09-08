package net.horizonsend.ion.server.listeners.ioncore

import net.horizonsend.ion.common.database.Achievement
import net.horizonsend.ion.core.events.HyperspaceEnterEvent
import net.horizonsend.ion.server.annotations.BukkitListener
import net.horizonsend.ion.server.utilities.rewardAchievement
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

@BukkitListener
@Suppress("Unused")
class HyperspaceEnterListener : Listener {
	@EventHandler(priority = EventPriority.LOWEST)
	fun onHyperspaceEnterEvent(event: HyperspaceEnterEvent) {
		event.player.rewardAchievement(Achievement.USE_HYPERSPACE)
	}
}
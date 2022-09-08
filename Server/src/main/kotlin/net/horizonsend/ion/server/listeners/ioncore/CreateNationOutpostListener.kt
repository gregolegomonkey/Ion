package net.horizonsend.ion.server.listeners.ioncore

import net.horizonsend.ion.common.database.Achievement
import net.horizonsend.ion.core.events.CreateNationOutpostEvent
import net.horizonsend.ion.server.annotations.BukkitListener
import net.horizonsend.ion.server.utilities.rewardAchievement
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

@BukkitListener
@Suppress("Unused")
class CreateNationOutpostListener : Listener {
	@EventHandler(priority = EventPriority.LOWEST)
	fun onCreateNationOutpostEvent(event: CreateNationOutpostEvent) {
		event.player.rewardAchievement(Achievement.CREATE_OUTPOST)
	}
}
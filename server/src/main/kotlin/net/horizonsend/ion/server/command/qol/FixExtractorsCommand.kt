package net.horizonsend.ion.server.command.qol

import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import com.sk89q.worldedit.WorldEdit
import net.horizonsend.ion.common.extensions.success
import net.horizonsend.ion.server.features.transport.Extractors
import net.horizonsend.ion.server.miscellaneous.utils.Vec3i
import net.horizonsend.ion.server.command.SLCommand
import org.bukkit.entity.Player

@CommandAlias("fixextractors")
@CommandPermission("ion.fixextractors")
object FixExtractorsCommand : SLCommand() {
	@Default
	@Suppress("unused")
	fun onFixExtractors(sender: Player) {
		val session = WorldEdit.getInstance().sessionManager.findByName(sender.name) ?: return
		val selection = session.getSelection(session.selectionWorld)

		var count = 0

		for (blockPosition in selection) {
			val x = blockPosition.x
			val y = blockPosition.y
			val z = blockPosition.z

			val block = sender.world.getBlockAt(x, y, z)

			if (block.type != Extractors.EXTRACTOR_BLOCK) continue

			val vec3i = Vec3i(x, y, z)

			if (Extractors.contains(sender.world, vec3i)) continue

			count++
			Extractors.add(sender.world, vec3i)
		}

		sender.success("Registered $count new extractors")
	}
}

package net.horizonsend.ion.server.features.multiblock.misc

import net.horizonsend.ion.common.database.schema.Cryopod
import net.horizonsend.ion.common.database.schema.misc.SLPlayer
import net.horizonsend.ion.common.extensions.information
import net.horizonsend.ion.common.extensions.userError
import net.horizonsend.ion.server.features.machine.CryoPods
import net.horizonsend.ion.server.features.multiblock.InteractableMultiblock
import net.horizonsend.ion.server.features.multiblock.Multiblock
import net.horizonsend.ion.server.features.multiblock.MultiblockShape
import net.horizonsend.ion.server.miscellaneous.utils.Vec3i
import net.horizonsend.ion.server.miscellaneous.utils.get
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.block.Sign
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

object CryoPodMultiblock : Multiblock(), InteractableMultiblock {
	override val name = "cryopod"

	override val signText = createSignText(
		line1 = "&1-[Cryo Pod]-",
		line2 = null,
		line3 = null,
		line4 = null
	)

	override fun MultiblockShape.buildStructure() {
		z(-2) {
			y(-1) {
				x(0).anyDoor()
			}
		}

		z(-1) {
			y(-2) {
				x(0).anyGlass()
			}

			y(-1) {
				x(-1).anyGlass()
				x(+1).anyGlass()
			}
			y(+0) {
				x(-1).anyGlass()
				x(+1).anyGlass()
			}

			y(+1) {
				x(0).anyGlass()
			}
		}

		z(+0) {
			y(-1) {
				x(0).anyGlass()
			}

			y(+0) {
				x(0).anyGlass()
			}
		}
	}

	override fun setupSign(player: Player, sign: Sign) {
		CryoPods.setCryoPod(player.uniqueId, sign.world.name, Vec3i(sign.location))

		super.setupSign(player, sign)
	}

	override fun onSignInteract(sign: Sign, player: Player, event: PlayerInteractEvent) {
		val pos = Vec3i(sign.location)
		if (!isOwner(sign, player)) {
			player.userError("You aren't the owner of this cryo pod!")
			return
		}

		val selectedCryo = SLPlayer[player].selectedCryopod?.let { Cryopod.findById(it) }

		if (event.action == Action.LEFT_CLICK_BLOCK) {
			if (selectedCryo?.vec3i() != pos) {
				player.userError("This is not your selected cryo pod!")
				return
			}

			CryoPods.removeCryoPod(player.uniqueId)
			player.information("Deselected cryo pod!")
		} else if (event.action == Action.RIGHT_CLICK_BLOCK) {
			if (selectedCryo?.vec3i() == pos) {
				player.userError("This is already your selected cryo pod!")
				return
			}

			CryoPods.setCryoPod(player.uniqueId, sign.world.name, pos)
			player.information("Selected cryo pod!")
		}
	}

	override fun onTransformSign(player: Player, sign: Sign) {
		sign.line(1, text("[PASSIVE]").color(NamedTextColor.GREEN))
		sign.line(2, text(player.uniqueId.toString()).decorate(TextDecoration.OBFUSCATED))
		sign.line(3, text(player.name))
	}

	fun isOwner(sign: Sign, player: Player): Boolean {
		return (sign.line(2) as TextComponent).content() == player.uniqueId.toString()
	}
}

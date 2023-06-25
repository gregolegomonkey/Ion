package net.horizonsend.ion.server.features.spacestations

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import net.horizonsend.ion.server.database.Oid
import net.horizonsend.ion.server.database.schema.misc.SLPlayerId
import net.horizonsend.ion.server.database.schema.nations.Nation
import net.horizonsend.ion.server.database.schema.nations.NationRole
import net.horizonsend.ion.server.database.schema.nations.Settlement
import net.horizonsend.ion.server.database.schema.nations.SettlementRole
import net.horizonsend.ion.server.database.schema.nations.spacestation.NationSpaceStation
import net.horizonsend.ion.server.database.schema.nations.spacestation.PlayerSpaceStation
import net.horizonsend.ion.server.database.schema.nations.spacestation.SettlementSpaceStation
import net.horizonsend.ion.server.database.schema.nations.spacestation.SpaceStation
import net.starlegacy.SLComponent
import net.starlegacy.util.optional
import java.util.Optional

object SpaceStations : SLComponent() {
	private val spaceStations = mutableListOf<CachedSpaceStation<*, *, *>>()

	private val nationSpaceStations = mutableListOf<CachedNationSpaceStation>()
	private val settlementSpaceStations = mutableListOf<CachedSettlementSpaceStation>()
	private val playerSpaceStations = mutableListOf<CachedPlayerSpaceStation>()

	val spaceStationCache: LoadingCache<String, Optional<CachedSpaceStation<*, *, *>>> =
		CacheBuilder.newBuilder().weakKeys().build(
			CacheLoader.from { name ->
				return@from optional(spaceStations.firstOrNull { it.name == name })
			}
		)

	enum class SpaceStationPermission(val nation: NationRole.Permission, val settlement: SettlementRole.Permission) {
		CREATE_STATION(NationRole.Permission.CREATE_STATION, SettlementRole.Permission.CREATE_STATION),
		MANAGE_STATION(NationRole.Permission.MANAGE_STATION, SettlementRole.Permission.MANAGE_STATION),
		DELETE_STATION(NationRole.Permission.DELETE_STATION, SettlementRole.Permission.DELETE_STATION)
	}

	enum class TrustLevel { NONE, MANUAL, SETTLEMENT_MEMBER, NATION_MEMBER, ALLY }

	override fun onEnable() {
		reload()
	}

	fun invalidate(station: SpaceStation<*>) {
		spaceStations.removeAll { it.databaseId == station._id }

		createCached(station)
	}

	fun reload() {
		spaceStations.clear()

		for (nationSpaceStation in NationSpaceStation.all()) {
			createCached(nationSpaceStation)
		}

		for (settlementSpaceStation in SettlementSpaceStation.all()) {
			createCached(settlementSpaceStation)
		}

		for (playerSpaceStation in PlayerSpaceStation.all()) {
			createCached(playerSpaceStation)
		}
	}

	fun createCached(station: SpaceStation<*>): CachedSpaceStation<*, *, *> {
		val cachedStation: CachedSpaceStation<*, *, *> = when (station) {
			is NationSpaceStation -> CachedNationSpaceStation(
				databaseId = station._id,
				owner = station.owner as Oid<Nation>,
				name = station.name,
				world = station.world,
				x = station.x,
				z = station.z,
				radius = station.radius,
				trustedPlayers = station.trustedPlayers,
				trustedSettlements = station.trustedSettlements,
				trustedNations = station.trustedNations,
				trustLevel = station.trustLevel,
			)

			is SettlementSpaceStation -> CachedSettlementSpaceStation(
				databaseId = station._id,
				owner = station.owner as Oid<Settlement>,
				name = station.name,
				world = station.world,
				x = station.x,
				z = station.z,
				radius = station.radius,
				trustedPlayers = station.trustedPlayers,
				trustedSettlements = station.trustedSettlements,
				trustedNations = station.trustedNations,
				trustLevel = station.trustLevel,
			)

			is PlayerSpaceStation -> CachedPlayerSpaceStation(
				databaseId = station._id,
				owner = station.owner as SLPlayerId,
				name = station.name,
				world = station.world,
				x = station.x,
				z = station.z,
				radius = station.radius,
				trustedPlayers = station.trustedPlayers,
				trustedSettlements = station.trustedSettlements,
				trustedNations = station.trustedNations,
				trustLevel = station.trustLevel,
			)

			else -> throw NotImplementedError()
		}

		spaceStations += cachedStation

		return cachedStation
	}
}

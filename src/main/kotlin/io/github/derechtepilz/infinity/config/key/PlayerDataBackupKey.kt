package io.github.derechtepilz.infinity.config.key

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.util.concurrent.TimeUnit

@Suppress("ReplaceManualRangeWithIndicesCalls")
class PlayerDataBackupKey(var interval: Int, timeUnit: String) : ConfigKey<PlayerDataBackupKey> {

	companion object {
		const val BACKUP_KEY = "player-data-backup"
	}

	private val validTimeUnits = listOf(TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.HOURS)

	var activeTimeUnit: TimeUnit = TimeUnit.valueOf(timeUnit.uppercase())

	override fun getBackupKey(): String {
		return BACKUP_KEY
	}

	override fun toJson(): JsonObject {
		val dataBackup = JsonObject()
		dataBackup.addProperty("interval", interval)

		val timeUnit = JsonArray()
		for (i in 0 until validTimeUnits.size) {
			val timeUnitSetting = JsonObject()
			timeUnitSetting.addProperty("active", activeTimeUnit == validTimeUnits[i])
			timeUnitSetting.addProperty("timeUnit", validTimeUnits[i].name.lowercase())
			timeUnit.add(timeUnitSetting)
		}

		dataBackup.add("timeUnit", timeUnit)

		return dataBackup
	}

	override fun equals(other: Any?): Boolean {
		if (other !is PlayerDataBackupKey) return false
		if (other.activeTimeUnit != this.activeTimeUnit) return false
		return other.interval == this.interval
	}

	override fun toString(): String {
		return "[interval=$interval|timeUnit=${activeTimeUnit.name}]"
	}

	override fun hashCode(): Int {
		var result = interval
		result = 31 * result + activeTimeUnit.hashCode()
		return result
	}

}
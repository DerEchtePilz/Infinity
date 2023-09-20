package io.github.derechtepilz.tests

import io.github.derechtepilz.TestBase
import io.github.derechtepilz.infinity.config.ConfigHandler
import io.github.derechtepilz.infinity.config.key.PlayerDataBackupKey
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class ConfigTests : TestBase() {

	@BeforeEach
	override fun setUp() {
		super.setUp()
	}

	@AfterEach
	override fun tearDown() {
		ConfigHandler.resetConfig()
		super.tearDown()
	}

	@Test
	fun retrievePlayerDataBackupTest() {
		// value to test against
		val playerDataBackup = PlayerDataBackupKey(10, TimeUnit.MINUTES.name.lowercase())

		val configDataBackup = ConfigHandler.getBackupInterval()

		assertEquals(playerDataBackup, configDataBackup)
	}

	@Test
	fun retrievePlayerDataBackupModifyTimeUnitTest() {
		// value to test against
		val playerDataBackup = PlayerDataBackupKey(10, TimeUnit.HOURS.name.lowercase())

		val configDataBackupDefault = ConfigHandler.getBackupInterval()
		configDataBackupDefault.activeTimeUnit = TimeUnit.HOURS

		assertEquals(playerDataBackup, configDataBackupDefault)

		// Check updating the config with new value succeeds by immediately retrieving it again
		ConfigHandler.setBackupInterval(configDataBackupDefault)

		val updatedDataBackup = ConfigHandler.getBackupInterval()

		assertEquals(playerDataBackup, updatedDataBackup)
	}

	@Test
	fun retrievePlayerDataBackupModifyIntervalTest() {
		// value to test against
		val playerDataBackup = PlayerDataBackupKey(30, TimeUnit.MINUTES.name.lowercase())

		val configDataBackupDefault = ConfigHandler.getBackupInterval()
		configDataBackupDefault.interval = 30

		assertEquals(playerDataBackup, configDataBackupDefault)

		// Check updating the config with new value succeeds by immediately retrieving it again
		ConfigHandler.setBackupInterval(configDataBackupDefault)

		val updatedDataBackup = ConfigHandler.getBackupInterval()

		assertEquals(playerDataBackup, updatedDataBackup)
	}

	@Test
	fun retrievePlayerDataBackupModifyIntervalAndTimeUnitTest() {
		// value to test against
		val playerDataBackup = PlayerDataBackupKey(30, TimeUnit.HOURS.name.lowercase())

		val configDataBackupDefault = ConfigHandler.getBackupInterval()
		configDataBackupDefault.interval = 30
		configDataBackupDefault.activeTimeUnit = TimeUnit.HOURS

		assertEquals(playerDataBackup, configDataBackupDefault)

		// Check updating the config with new value succeeds by immediately retrieving it again
		ConfigHandler.setBackupInterval(configDataBackupDefault)

		val updatedDataBackup = ConfigHandler.getBackupInterval()

		assertEquals(playerDataBackup, updatedDataBackup)
	}

}
package io.github.derechtepilz

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import org.bukkit.Bukkit

abstract class TestBase {

	lateinit var server: ServerMock
	lateinit var plugin: Main

	open fun setUp() {
		this.server = MockBukkit.mock()
		this.plugin = MockBukkit.load(Main::class.java)
	}

	open fun tearDown() {
		Bukkit.getScheduler().cancelTasks(plugin)
		MockBukkit.unmock()
	}

}
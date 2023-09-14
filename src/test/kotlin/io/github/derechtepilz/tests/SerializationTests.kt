package io.github.derechtepilz.tests

import be.seeseemelk.mockbukkit.UnimplementedOperationException
import io.github.derechtepilz.TestBase
import io.github.derechtepilz.infinity.Infinity
import io.github.derechtepilz.infinity.Registry
import io.github.derechtepilz.infinity.gamemode.serializer.EffectSerializer
import io.github.derechtepilz.infinity.gamemode.serializer.ExperienceSerializer
import io.github.derechtepilz.infinity.gamemode.serializer.HealthHungerSerializer
import io.github.derechtepilz.infinity.gamemode.serializer.InventorySerializer
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import java.util.logging.Level
import kotlin.random.Random

@Suppress("ReplaceManualRangeWithIndicesCalls")
class SerializationTests : TestBase() {

	@BeforeEach
	override fun setUp() {
		super.setUp()
	}

	@AfterEach
	override fun tearDown() {
		super.tearDown()
	}

	@Test // Currently skipped
	fun saveAndLoadInventorySerializationTest() {
		val player = server.addPlayer()

		val pickaxe = Registry.Item.getItem("infinity_pickaxe", -1)
		val axeFarming = Registry.Item.getItem("infinity_axe", 0)
		player.inventory.setItem(0, pickaxe)
		player.enderChest.setItem(0, axeFarming)

		val serializedInventoryAndEnderChest = InventorySerializer.serialize(player) // Test is skipped because this uses ItemStack#serializeAsBytes() which is unimplemented in MockBukkit

		val deserializedInventoryAndEnderChest = InventorySerializer.deserialize(serializedInventoryAndEnderChest)

		val inventory = deserializedInventoryAndEnderChest[0]
		val enderChest = deserializedInventoryAndEnderChest[1]

		// Check correct inventory sizes
		assertTrue(inventory.size == 40)
		assertTrue(enderChest.size == 27)

		// Check the items are in the correct spots
		assertEquals(pickaxe, inventory[0])
		assertEquals(axeFarming, enderChest[0])

		// Verify there's only one non-null item
		var nonNullItems = 0
		for (i in 0 until inventory.size) {
			nonNullItems = if (inventory[i] != null) nonNullItems + 1 else nonNullItems
		}

		// Check that the player inventory only has one non-null item
		assertTrue(nonNullItems == 1)

		nonNullItems = 0
		for (i in 0 until enderChest.size) {
			nonNullItems = if (enderChest[i] != null) nonNullItems + 1 else nonNullItems
		}

		// Check that the ender chest only has one non-null item
		assertTrue(nonNullItems == 1)
	}

	@RepeatedTest(5)
	fun healthHungerSerializationTest() {
		val player = server.addPlayer()

		player.health = Random.nextDouble(0.5, 20.0)
		player.foodLevel = Random.nextInt(1, 20)
		player.saturation = Random.nextFloat() + 5.0f

		// Values to check against
		val health = player.health
		val foodLevel = player.foodLevel
		val saturation = player.saturation

		val healthHungerSerializedString = HealthHungerSerializer.serialize(player)

		val deserializedHealthHunger = HealthHungerSerializer.deserialize(healthHungerSerializedString)

		val deserializedHealth = deserializedHealthHunger[0] as Double
		val deserializedFoodLevel = deserializedHealthHunger[1] as Int
		val deserializedSaturation = deserializedHealthHunger[2] as Float

		assertEquals(health, deserializedHealth)
		assertEquals(foodLevel, deserializedFoodLevel)
		assertEquals(saturation, deserializedSaturation)
	}

	@RepeatedTest(5)
	fun experienceSerializationTest() {
		val player = server.addPlayer()

		player.level = Random.nextInt(2, 1024)
		player.exp = Random.nextFloat()

		// Values to check against
		val level = player.level
		val exp = player.exp

		val experienceSerializedString = ExperienceSerializer.serialize(player)
		val deserializedExperience = ExperienceSerializer.deserialize(experienceSerializedString)

		val deserializedLevel = deserializedExperience[0] as Int
		val deserializedExp = deserializedExperience[1] as Float

		assertEquals(level, deserializedLevel)
		assertEquals(exp, deserializedExp)
	}

	@Test
	fun effectSerializationTest() {
		val player = server.addPlayer()

		// Potion effects to check against
		val potionEffects = mutableListOf(
			PotionEffect(PotionEffectType.SPEED, 120, 5),
			PotionEffect(PotionEffectType.INCREASE_DAMAGE, 25, 3)
		)

		player.addPotionEffects(potionEffects)

		val potionEffectsSerializedString = EffectSerializer.serialize(player)
		val deserializedPotionEffects = EffectSerializer.deserialize(potionEffectsSerializedString)

		assertEquals(potionEffects, deserializedPotionEffects)
	}

}
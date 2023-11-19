/*
 *  Infinity - a Minecraft story-game for Paper servers
 *  Copyright (C) 2023  DerEchtePilz
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.derechtepilz.infinity.world

import org.bukkit.*
import org.bukkit.block.Biome
import org.bukkit.generator.BiomeProvider
import org.bukkit.generator.ChunkGenerator
import org.bukkit.generator.WorldInfo
import java.util.*

class WorldManager {

	class ChunkGenerators {

		class EmptyChunkGenerator : ChunkGenerator()

		class NetherChunkGenerator : ChunkGenerator() {
			override fun generateBedrock(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData) {
				for (x in 0..15) {
					for (z in 0..15) {
						chunkData.setBlock(x, chunkData.minHeight, z, Material.BEDROCK)
						chunkData.setBlock(x, chunkData.minHeight + 1, z, Material.BEDROCK)
						chunkData.setBlock(x, chunkData.minHeight + 2, z, Material.BEDROCK)
						chunkData.setBlock(x, chunkData.minHeight + 3, z, Material.BEDROCK)
						chunkData.setBlock(x, chunkData.minHeight + 4, z, Material.BEDROCK)
					}
				}
			}

			override fun generateNoise(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData) {
				for (y in chunkData.minHeight + 5..chunkData.minHeight + 100) {
					for (x in 0..15) {
						for (z in 0..15) {
							chunkData.setBlock(x, y, z, Material.LAVA)
						}
					}
				}
			}

		}

		class StoneChunkGenerator : ChunkGenerator() {

			override fun generateBedrock(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData) {
				for (x in 0..15) {
					for (z in 0..15) {
						chunkData.setBlock(x, chunkData.minHeight, z, Material.BEDROCK)
						chunkData.setBlock(x, chunkData.minHeight + 1, z, Material.BEDROCK)
						chunkData.setBlock(x, chunkData.minHeight + 2, z, Material.BEDROCK)
						chunkData.setBlock(x, chunkData.minHeight + 3, z, Material.BEDROCK)
						chunkData.setBlock(x, chunkData.minHeight + 4, z, Material.BEDROCK)

						chunkData.setBlock(x, chunkData.maxHeight - 4, z, Material.BEDROCK)
						chunkData.setBlock(x, chunkData.maxHeight - 3, z, Material.BEDROCK)
						chunkData.setBlock(x, chunkData.maxHeight - 2, z, Material.BEDROCK)
						chunkData.setBlock(x, chunkData.maxHeight - 1, z, Material.BEDROCK)
						chunkData.setBlock(x, chunkData.maxHeight, z, Material.BEDROCK)
					}
				}
			}

			override fun generateNoise(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData) {
				val minY = chunkData.minHeight + 5
				val maxY = chunkData.maxHeight - 5
				for (y in minY..maxY) {
					for (x in 0..15) {
						for (z in 0..15) {
							chunkData.setBlock(x, y, z, Material.STONE)
						}
					}
				}
			}

		}

	}

	class BiomeProviders {

		class EmptyBiomeProvider : BiomeProvider() {
			override fun getBiome(worldInfo: WorldInfo, x: Int, y: Int, z: Int): Biome {
				return Biome.PLAINS
			}

			override fun getBiomes(worldInfo: WorldInfo): MutableList<Biome> {
				return mutableListOf(Biome.PLAINS)
			}

		}

		class StoneBiomeProvider : BiomeProvider() {
			override fun getBiome(worldInfo: WorldInfo, x: Int, y: Int, z: Int): Biome {
				return Biome.PLAINS
			}

			override fun getBiomes(worldInfo: WorldInfo): MutableList<Biome> {
				return mutableListOf(Biome.PLAINS)
			}

		}

		class NetherBiomeProvider : BiomeProvider() {

			override fun getBiome(worldInfo: WorldInfo, x: Int, y: Int, z: Int): Biome {
				return Biome.NETHER_WASTES
			}

			override fun getBiomes(worldInfo: WorldInfo): MutableList<Biome> {
				return mutableListOf(Biome.NETHER_WASTES)
			}

		}

	}

}
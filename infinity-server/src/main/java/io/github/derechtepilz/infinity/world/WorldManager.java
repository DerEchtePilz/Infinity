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

package io.github.derechtepilz.infinity.world;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class WorldManager {

	public static class ChunkGenerators {

		public static class EmptyChunkGenerator extends ChunkGenerator {
		}

		public static class NetherChunkGenerator extends ChunkGenerator {

			@Override
			public void generateBedrock(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
				for (int x = 0; x <= 15; x++) {
					for (int z = 0; z <= 15; z++) {
						chunkData.setBlock(x, chunkData.getMinHeight(), z, Material.BEDROCK);
						chunkData.setBlock(x, chunkData.getMinHeight(), z + 1, Material.BEDROCK);
						chunkData.setBlock(x, chunkData.getMinHeight(), z + 2, Material.BEDROCK);
						chunkData.setBlock(x, chunkData.getMinHeight(), z + 3, Material.BEDROCK);
						chunkData.setBlock(x, chunkData.getMinHeight(), z + 4, Material.BEDROCK);
					}
				}
			}

			@Override
			public void generateNoise(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
				for (int y = chunkData.getMinHeight() + 5; y <= chunkData.getMinHeight() + 100; y++) {
					for (int x = 0; x <= 15; x++) {
						for (int z = 0; z <= 15; z++) {
							chunkData.setBlock(x, y, z, Material.LAVA);
						}
					}
				}
			}
		}

		public static class StoneChunkGenerator extends ChunkGenerator {

			@Override
			public void generateBedrock(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
				for (int x = 0; x <= 15; x++) {
					for (int z = 0; z <= 15; z++) {
						chunkData.setBlock(x, chunkData.getMinHeight(), z, Material.BEDROCK);
						chunkData.setBlock(x, chunkData.getMinHeight() + 1, z, Material.BEDROCK);
						chunkData.setBlock(x, chunkData.getMinHeight() + 2, z, Material.BEDROCK);
						chunkData.setBlock(x, chunkData.getMinHeight() + 3, z, Material.BEDROCK);
						chunkData.setBlock(x, chunkData.getMinHeight() + 4, z, Material.BEDROCK);

						chunkData.setBlock(x, chunkData.getMaxHeight() - 4, z, Material.BEDROCK);
						chunkData.setBlock(x, chunkData.getMaxHeight() - 3, z, Material.BEDROCK);
						chunkData.setBlock(x, chunkData.getMaxHeight() - 2, z, Material.BEDROCK);
						chunkData.setBlock(x, chunkData.getMaxHeight() - 1, z, Material.BEDROCK);
						chunkData.setBlock(x, chunkData.getMaxHeight(), z, Material.BEDROCK);
					}
				}
			}

			@Override
			public void generateNoise(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
				int minY = chunkData.getMinHeight() + 5;
				int maxY = chunkData.getMaxHeight() - 5;
				for (int y = minY; y <= maxY; y++) {
					for (int x = 0; x <= 15; x++) {
						for (int z = 0; z <= 15; z++) {
							chunkData.setBlock(x, y, z, Material.STONE);
						}
					}
				}
			}

		}

	}

	public static class BiomeProviders {

		public static class EmptyBiomeProvider extends BiomeProvider {

			@Override
			public @NotNull Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) {
				return Biome.PLAINS;
			}

			@Override
			public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
				return List.of(Biome.PLAINS);
			}
		}

		public static class StoneBiomeProvider extends BiomeProvider {

			@Override
			public @NotNull Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) {
				return Biome.PLAINS;
			}

			@Override
			public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
				return List.of(Biome.PLAINS);
			}
		}

		public static class NetherBiomeProvider extends BiomeProvider {

			@Override
			public @NotNull Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) {
				return Biome.NETHER_WASTES;
			}

			@Override
			public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
				return List.of(Biome.NETHER_WASTES);
			}

		}

	}

}

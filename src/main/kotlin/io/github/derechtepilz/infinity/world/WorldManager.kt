package io.github.derechtepilz.infinity.world

import org.bukkit.*
import org.bukkit.generator.ChunkGenerator
import org.bukkit.generator.WorldInfo
import java.util.*

class WorldManager {

    class Lobby : ChunkGenerator()

    class Nether : ChunkGenerator() {
        override fun generateBedrock(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData) {
            for (x in 0 .. 15) {
                for (z in 0 .. 15) {
                    chunkData.setBlock(x, chunkData.minHeight, z, Material.BEDROCK)
                    chunkData.setBlock(x, chunkData.minHeight + 1, z, Material.BEDROCK)
                    chunkData.setBlock(x, chunkData.minHeight + 2, z, Material.BEDROCK)
                    chunkData.setBlock(x, chunkData.minHeight + 3, z, Material.BEDROCK)
                    chunkData.setBlock(x, chunkData.minHeight + 4, z, Material.BEDROCK)
                }
            }
        }

        override fun generateNoise(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData) {
            for (y in chunkData.minHeight + 5 .. chunkData.minHeight + 64) {
                for (x in 0..15) {
                    for (z in 0..15) {
                        chunkData.setBlock(x, y, z, Material.LAVA)
                    }
                }
            }
        }

    }

    class Stone : ChunkGenerator() {

        override fun generateBedrock(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData) {
            for (x in 0 .. 15) {
                for (z in 0 .. 15) {
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

    }

}
package io.github.derechtepilz.infinity.util

import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.util.BlockIterator
import org.bukkit.util.NumberConversions
import org.bukkit.util.Vector
import org.bukkit.util.NumberConversions.floor

class BlockTracer(private val player: Player) : Runnable {

    companion object {
        var lastBlock: Block? = null
        val replacedBlocks: MutableMap<Location, Material> = mutableMapOf()
    }

    override fun run() {
        // Problems:
        // Multi blocks -> Doors, Tall grass, beds, etc.
        // -> ???
        // Placed block is breakable and breaking it fails to replace the block
        // -> block should be unbreakable, might be fixed by adding a BlockBreakEvent or just placing bedrock
        // -> this is for testing anyway since the final system won't have replaced blocks and will just return the info of the block
        // -> making all the other points invalid
        // Preserve block states -> crops and stairs get their "default(?)" values

        val targetBlock = player.getTargetBlock()
        /*
        if (lastBlock == null) {
            lastBlock = targetBlock.state.block

            replacedBlocks[lastBlock!!.location] = lastBlock!!.type
            targetBlock.type = Material.REDSTONE_BLOCK
            return
        }
        if (lastBlock != targetBlock) {
            lastBlock!!.type = replacedBlocks[lastBlock!!.location]!!
            replacedBlocks.remove(lastBlock!!.location)

            lastBlock = targetBlock.state.block

            replacedBlocks[lastBlock!!.location] = lastBlock!!.type
            targetBlock.type = Material.REDSTONE_BLOCK
            return
        }
         */
    }
}

fun LivingEntity.getTargetBlock0(): Block {
    val blockIterator = BlockIterator(this.world, this.eyeLocation.toVector(), this.eyeLocation.direction, 0.0, 0)
    var currentBlock = this.location.block
    while (blockIterator.hasNext()) {
        currentBlock = blockIterator.next()
        if (currentBlock.type == Material.AIR || currentBlock.type == Material.CAVE_AIR) {
            continue
        }
        break
    }
    return currentBlock
}

fun LivingEntity.getTargetBlock(): Block {
    val viewDirection = this.eyeLocation.toVector().clone()
    val startBlock = this.world.getBlockAt(floor(viewDirection.x), floor(viewDirection.y), floor(viewDirection.z))
    // Omitted, the correct block is printed // println(startBlock)

    // Create a clone of the view direction vector
    var start = viewDirection.clone()
    var block = this.world.getBlockAt(floor(start.x), floor(start.y), floor(start.z))
    // Omitted, the correct block is printed // println(block) // Should be the same as before

    start = start.add(start.clone().normalize())
    var block2 = this.world.getBlockAt(floor(start.x), floor(start.y), floor(start.z))
    println(block2) // When looking down directly, this block should have the same coordinates as my feet


    var targetedBlock = this.world.getBlockAt(-64, 0, 0) // viewDirection.add(viewDirectionNormalized).toLocation(this.world).block
    return targetedBlock
}

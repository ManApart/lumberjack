package org.manapart.lumberjack

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.entity.item.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.loot.LootContext
import net.minecraft.loot.LootParameters
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.vector.Vector3d
import net.minecraft.world.IWorld
import net.minecraft.world.World
import net.minecraft.world.server.ServerWorld
import net.minecraftforge.common.ToolType
import net.minecraftforge.event.world.BlockEvent.BreakEvent
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager
import thedarkcolour.kotlinforforge.forge.FORGE_BUS

@Mod("lumberjack")
object Lumberjack {
    private val log = LogManager.getLogger()

    init {
        FORGE_BUS.addListener(::onBreak)
    }

    private fun onBreak(event: BreakEvent) {
        val block = event.state.block
        if (shouldFellTrees(event.player)) {
            if (isLog(block)) {
                log.info(event.player.name.contents.toString() + " broke " + block + " at " + event.pos)
                fellLogs(event.pos, event.world, event.player.mainHandItem)
            }
        }
    }

    private fun shouldFellTrees(player: PlayerEntity): Boolean {
        return !player.isCrouching && player.mainHandItem.item.getToolTypes(player.mainHandItem).contains(ToolType.AXE)
    }

    private fun fellLogs(sourcePosition: BlockPos, world: IWorld, tool: ItemStack) {
        val finder = ColumnFinder(sourcePosition, world)
        val columns = finder.findColumns()
        chopColumns(columns, 1, world, tool)
    }

    private fun chopColumns(columns: ArrayList<BlockPos>, y: Int, world: IWorld, tool: ItemStack) {
        var atLeastOneBlockHarvested = false
        for (column in columns) {
            val pos = column.offset(0, y, 0)
            val blockState = world.getBlockState(pos)
            val block = blockState.block
            if (isLog(block) || isLeaves(block)) {
                dropBlock(world, pos, blockState, tool)
                atLeastOneBlockHarvested = true
            }
        }
        if (atLeastOneBlockHarvested) {
            chopColumns(columns, y + 1, world, tool)
        }
    }

    private fun dropBlock(world: IWorld, pos: BlockPos, state: BlockState, tool: ItemStack) {
        if (world is ServerWorld) {
            val origin = Vector3d.atCenterOf(pos)
            val lootContext = LootContext.Builder(world)
                .withParameter(LootParameters.TOOL, tool)
                .withParameter(LootParameters.ORIGIN, origin)

            val drops = state.getDrops(lootContext).filterNotNull()
            world.removeBlock(pos, false)
            if (drops.isNotEmpty()) {
                dropItems(world, pos, drops)
            }
        }
    }

    private fun dropItems(world: IWorld, pos: BlockPos, drops: List<ItemStack>) {
        drops.forEach { drop ->
            val dropItem = ItemEntity(world as World, pos.x.toDouble() + 0.5, pos.y.toDouble() + 0.5, pos.z.toDouble() + 0.5, drop)
            world.addFreshEntity(dropItem)
        }
    }

}

fun isLog(block: Block): Boolean {
    return isType(block, "log")
}

fun isLeaves(block: Block): Boolean {
    return isType(block, "leaves")
}

private fun isType(block: Block, type: String): Boolean {
    return if (block.registryName != null) {
        block.registryName!!.path.toLowerCase().contains(type)
    } else false
}
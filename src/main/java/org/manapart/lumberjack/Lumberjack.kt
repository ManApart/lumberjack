package org.manapart.lumberjack

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.Vec3
import net.minecraftforge.common.ToolActions
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
                fellLogs(event.pos, event.world as Level, event.player.mainHandItem)
            }
        }
    }

    private fun shouldFellTrees(player: Player): Boolean {
        return !player.isCrouching && player.mainHandItem.item.canPerformAction(player.mainHandItem, ToolActions.AXE_DIG)
    }

    private fun fellLogs(sourcePosition: BlockPos, world: Level, tool: ItemStack) {
        val finder = ColumnFinder(sourcePosition, world)
        val columns = finder.findColumns()
        chopColumns(columns, 1, world, tool)
    }

    private fun chopColumns(columns: ArrayList<BlockPos>, y: Int, world: Level, tool: ItemStack) {
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

    private fun dropBlock(world: Level, pos: BlockPos, state: BlockState, tool: ItemStack) {
        if (world is ServerLevel) {
            val origin = Vec3.atCenterOf(pos)
            val lootContext = LootContext.Builder(world)
                .withParameter(LootContextParams.TOOL, tool)
                .withParameter(LootContextParams.ORIGIN, origin)

            val drops = state.getDrops(lootContext).filterNotNull()
            world.removeBlock(pos, false)
            if (drops.isNotEmpty()) {
                dropItems(world, pos, drops)
            }
        }
    }

    private fun dropItems(world: Level, pos: BlockPos, drops: List<ItemStack>) {
        drops.forEach { drop ->
            val dropItem = ItemEntity(world, pos.x.toDouble() + 0.5, pos.y.toDouble() + 0.5, pos.z.toDouble() + 0.5, drop)
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
    return block.descriptionId.lowercase().contains(type)
}
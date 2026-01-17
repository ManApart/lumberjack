package org.manapart.lumberjack

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.AxeItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.slf4j.LoggerFactory

object Lumberjack : ModInitializer {
    private val log = LoggerFactory.getLogger("lumberjack")

    override fun onInitialize() {
        PlayerBlockBreakEvents.AFTER.register(::onBreak)
    }

    private fun onBreak(level: Level, player: Player, pos: BlockPos, state: BlockState, entity: BlockEntity?) {
        if (shouldFellTrees(player)) {
            if (state.block.isLog()) {
//                log.info(player.name.contents.toString() + " broke " + state.block + " at " + pos)
                fellLogs(pos, TestableWorld(level), player.mainHandItem)
            }
        }
    }

    private fun shouldFellTrees(player: Player): Boolean {
        return !player.isCrouching && player.mainHandItem.item is AxeItem
    }

    fun fellLogs(sourcePosition: BlockPos, world: TestableWorld, tool: ItemStack?) {
//        val finder = ColumnFinder(sourcePosition, world)
//        val columns = finder.findColumns()
//        chopColumns(columns, 1, world, tool)
        val (logs, leaves) = climbTree(world, sourcePosition)
        chopTree(world, logs, leaves, tool)
    }

//    private fun chopColumns(columns: List<BlockPos>, y: Int, world: TestableWorld, tool: ItemStack?) {
//        var atLeastOneBlockHarvested = false
//        for (column in columns) {
//            val pos = column.offset(0, y, 0)
//            if (world.isLog(pos) || world.isLeaves(pos)) {
//                dropBlock(world, pos, tool)
//                atLeastOneBlockHarvested = true
//            }
//        }
//        if (atLeastOneBlockHarvested) {
//            chopColumns(columns, y + 1, world, tool)
//        }
//    }
//
//    private fun dropBlock(world: TestableWorld, pos: BlockPos, tool: ItemStack?) {
//        if (world.level != null && world.level is ServerLevel) {
//            val origin = Vec3.atCenterOf(pos)
//            world.removeBlock(pos, false)
//            if (tool != null) {
//                val lootContext = LootParams.Builder(world.level)
//                    .withParameter(LootContextParams.TOOL, tool)
//                    .withParameter(LootContextParams.ORIGIN, origin)
//
//                val drops = world.level.getBlockState(pos).getDrops(lootContext).filterNotNull()
//                if (drops.isNotEmpty()) {
//                    dropItems(world.level, pos, drops)
//                }
//            }
//        } else {
//            world.removeBlock(pos, false)
//        }
//    }
//
//    private fun dropItems(world: Level, pos: BlockPos, drops: List<ItemStack>) {
//        drops.forEach { drop ->
//            val dropItem = ItemEntity(world, pos.x.toDouble() + 0.5, pos.y.toDouble() + 0.5, pos.z.toDouble() + 0.5, drop)
//            world.addFreshEntity(dropItem)
//        }
//    }

}

fun Block.isLog(): Boolean {
    return isType(this, "log")
}

fun Block.isLeaves(): Boolean {
    return isType(this, "leaves")
}

private fun isType(block: Block, type: String): Boolean {
    return block.descriptionId.lowercase().contains(type)
}

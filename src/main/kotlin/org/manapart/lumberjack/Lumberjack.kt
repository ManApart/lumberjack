package org.manapart.lumberjack

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.AxeItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.Vec3
import org.slf4j.LoggerFactory

object Lumberjack : ModInitializer {
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
        val (logs, leaves) = climbTree(world, sourcePosition)
        chopTree(world, logs, leaves, tool)
    }

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

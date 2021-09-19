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
import thedarkcolour.kotlinforforge.forge.FORGE_BUS

// The value here should match an entry in the META-INF/mods.toml file
@Mod("lumberjack")
object Lumberjack {

    init {
        FORGE_BUS.addListener(::onBreak)
    }

    private fun onBreak(event: BreakEvent) {
        println("On Break")
        val block = event.state.block
        val pos = event.pos
        if (shouldFellTrees(event.player)) {
            if (isLog(block)) {
                println(event.player.name.toString() + " broke " + block + " at " + pos)
                fellLogs(pos, event.world, event.player.mainHandItem)
            }
        }
    }

    private fun shouldFellTrees(player: PlayerEntity): Boolean {
        val item = player.mainHandItem.item
        return !player.isCrouching && item.getToolTypes(player.mainHandItem).contains(ToolType.AXE)
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
            val lootContext = LootContext.Builder(world)
            lootContext.withParameter(LootParameters.TOOL, tool)
            val origin = Vector3d.atCenterOf(pos)
            lootContext.withParameter(LootParameters.ORIGIN, origin)
            val drops = state.getDrops(lootContext)
            world.removeBlock(pos, false)
            if (drops.size >= 1) {
                dropItems(world, pos, drops)
            }
        }
    }

    private fun dropItems(world: IWorld, pos: BlockPos, drops: List<ItemStack?>) {
        for (i in drops) {
            if (i != null) {
                val dropItem = ItemEntity(world as World, pos.x.toDouble() + 0.5, pos.y.toDouble() + 0.5, pos.z.toDouble() + 0.5, i)
                //                ItemEntity dropItem = new ItemEntity(world.getWorld(), (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, i);
                world.addFreshEntity(dropItem)
            }
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
package org.manapart.lumberjack

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.Vec3

typealias Logs = Set<BlockPos>
typealias Leaves = Set<BlockPos>

fun climbTree(world: TestableWorld, source: BlockPos): Pair<Logs, Leaves> {
    val logs = mutableSetOf<BlockPos>()
    val leaves = mutableSetOf<BlockPos>()
    val open = mutableSetOf<BlockPos>()
    val closed = mutableSetOf<BlockPos>()
    logs.add(source)
    open.addAll(get3x3Layer(source.x, source.y + 1, source.z))
    while (open.isNotEmpty()) {
        val current = open.last()
        open.remove(current)
        if (!closed.contains(current)) {
            if (world.isLog(current)) {
                logs.add(current)
                open.addAll(get3x3Layer(current.x, current.y, current.z))
                open.addAll(get3x3Layer(current.x, current.y + 1, current.z))
            }

            if (world.isLeaves(current)) {
                leaves.add(current)
                open.addAll(get3x3Layer(current.x, current.y + 1, current.z))
                //Get any neighbor leaves, but don't build off of them
                get3x3Layer(current.x, current.y, current.z).forEach { n ->
                    if (!closed.contains(n)) {
                        if (world.isLeaves(n)) {
                            leaves.add(n)
                            closed.add(n)
                        }
                    }
                }
            }
            closed.add(current)
        }
    }
    return logs to leaves
}


fun chopTree(world: TestableWorld, logs: Logs, leaves: Leaves, tool: ItemStack?) {
    logs.forEach { world.removeBlock(it, tool) }
    leaves.filter { it.shouldDrop(world, logs) }.forEach { world.removeBlock(it, tool) }
}

//If any neighbor is a foreign log, don't drop
private fun BlockPos.shouldDrop(world: TestableWorld, logs: Logs): Boolean {
    val neighbors = listOf(BlockPos(x, y - 1, z)) + get3x3Layer(x, y, z)
    return neighbors.none { n -> !logs.contains(n) && world.isLog(n) }
}

fun removeBlockFromLevel(level: LevelAccessor, pos: BlockPos, tool: ItemStack?) {
    if (level is ServerLevel) {
        val origin = Vec3.atCenterOf(pos)
        if (tool != null) {
            val lootContext = LootParams.Builder(level)
                .withParameter(LootContextParams.TOOL, tool)
                .withParameter(LootContextParams.ORIGIN, origin)

            val drops = level.getBlockState(pos).getDrops(lootContext).filterNotNull()
            if (drops.isNotEmpty()) {
                dropItems(level, pos, drops)
            }
        }
        level.removeBlock(pos, false)
    }
}

private fun dropItems(world: Level, pos: BlockPos, drops: List<ItemStack>) {
    drops.forEach { drop ->
        val dropItem = ItemEntity(world, pos.x.toDouble() + 0.5, pos.y.toDouble() + 0.5, pos.z.toDouble() + 0.5, drop)
        world.addFreshEntity(dropItem)
    }
}

private fun get3x3Layer(x: Int, y: Int, z: Int): List<BlockPos> {
    return listOf(
        BlockPos(x - 1, y, z),
        BlockPos(x, y, z),
        BlockPos(x + 1, y, z),

        BlockPos(x - 1, y, z - 1),
        BlockPos(x, y, z - 1),
        BlockPos(x + 1, y, z - 1),

        BlockPos(x - 1, y, z + 1),
        BlockPos(x, y, z + 1),
        BlockPos(x + 1, y, z + 1),
    )
}

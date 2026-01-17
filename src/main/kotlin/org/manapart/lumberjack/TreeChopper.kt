package org.manapart.lumberjack

import net.minecraft.core.BlockPos
import net.minecraft.world.item.ItemStack

typealias Logs = Set<BlockPos>
typealias Leaves = Set<BlockPos>

fun climbTree(world: TestableWorld, source: BlockPos): Pair<Logs, Leaves> {
    val logs = mutableSetOf<BlockPos>()
    val leaves = mutableSetOf<BlockPos>()
    val open = mutableSetOf<BlockPos>()
    val closed = mutableSetOf<BlockPos>()
    logs.add(source)
    open.addAll(get3x3Grid(source.x, source.y + 1, source.z))
    while (open.isNotEmpty()) {
        val current = open.last()
        open.remove(current)
        if (!closed.contains(current)) {
            if (world.isLog(current)) {
                logs.add(current)
                open.addAll(get3x3Grid(current.x, current.y + 1, current.z))
            }

            if (world.isLeaves(current)) {
                leaves.add(current)
                open.addAll(get3x3Grid(current.x, current.y + 1, current.z))
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
    return get3x3Grid(x,y,z).none { n -> !logs.contains(n) && world.isLog(n) }
}

private fun get3x3Grid(x: Int, y: Int, z: Int): List<BlockPos> {
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

package org.manapart.lumberjack

import net.minecraft.core.BlockPos
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.chunk.LightChunk


private const val RADIUS = 3
private const val DIAMETER = RADIUS * 2 + 1

class ColumnFinder(private val source: BlockPos, private val world: TestableWorld) {
    fun findColumns(): List<BlockPos> {
        val baseBlocks = getBaseBlocks()
        denoteTrunks(baseBlocks)
        denoteHarvestableBlocks(baseBlocks)
        return filterHarvestable(baseBlocks)
    }

    private fun getBaseBlocks(): Array<Array<BaseBlock?>> {
        val baseBlocks = Array(DIAMETER) { arrayOfNulls<BaseBlock>(DIAMETER) }
        for (x in 0 until DIAMETER) {
            for (z in 0 until DIAMETER) {
                val pos = source.offset(x - RADIUS, 0, z - RADIUS)
                val isLog = world.isLog(pos)
                baseBlocks[x][z] = BaseBlock(x, z, pos.x, pos.z, isLog)
                if (!isLog) {
                    baseBlocks[x][z]!!.isTrunk = false
                }
            }
        }
        return baseBlocks
    }

    private fun denoteTrunks(baseBlocks: Array<Array<BaseBlock?>>) {
        val open = ArrayList<BaseBlock?>()
        val closed = HashSet<BaseBlock>()
        open.add(baseBlocks[RADIUS][RADIUS])
        while (open.isNotEmpty()) {
            val current = open[open.size - 1]
            open.remove(current)
            if (current != null && !closed.contains(current)) {
                closed.add(current)
                if (current.isTree) {
                    current.isTrunk = true
                    open.add(getBaseBlock(current.localX + 1, current.localZ, baseBlocks))
                    open.add(getBaseBlock(current.localX - 1, current.localZ, baseBlocks))
                    open.add(getBaseBlock(current.localX, current.localZ + 1, baseBlocks))
                    open.add(getBaseBlock(current.localX, current.localZ - 1, baseBlocks))
                }
            }
        }
    }

    private fun getBaseBlock(x: Int, z: Int, baseBlocks: Array<Array<BaseBlock?>>): BaseBlock? {
        return if (x > 0 && z > 0 && x < DIAMETER && z < DIAMETER) baseBlocks[x][z] else null
    }

    private fun denoteHarvestableBlocks(baseBlocks: Array<Array<BaseBlock?>>) {
        val trunks = ArrayList<BaseBlock>()
        val neighbors = ArrayList<BaseBlock>()
        val bufferBlocks = ArrayList<BaseBlock>()
        for (x in 0 until DIAMETER) {
            for (z in 0 until DIAMETER) {
                val block = baseBlocks[x][z]
                if (block != null) {
                    if (block.isTree) {
                        if (block.isTrunk) {
                            trunks.add(block)
                        } else {
                            neighbors.add(block)
                        }
                    } else {
                        bufferBlocks.add(block)
                    }
                }
            }
        }
        for (block in bufferBlocks) {
            if (neighbors.isEmpty()) {
                block.setHarvestable(true)
            } else {
                val trunkDistance = getMinDistance(block, trunks)
                val neighborDistance = getMinDistance(block, neighbors)
                block.setHarvestable(trunkDistance <= neighborDistance)
            }
        }
    }

    private fun getMinDistance(block: BaseBlock, blocks: ArrayList<BaseBlock>): Int {
        var min = DIAMETER
        for (other in blocks) {
            val dist = block.getDistance(other)
            if (dist < min) {
                min = dist
            }
        }
        return min
    }

    private fun filterHarvestable(baseBlocks: Array<Array<BaseBlock?>>): List<BlockPos> {
        return baseBlocks.flatMap { chunk -> chunk.filterNotNull().filter { it.shouldHarvest() } }.map { BlockPos(it.worldX, source.y, it.worldZ) }
    }

}

package org.manapart.lumberjack

import net.minecraft.core.BlockPos
import net.minecraft.world.level.LevelAccessor

interface FakeWorldShim {
    fun isLog(pos: BlockPos): Boolean
    fun isLeaves(pos: BlockPos): Boolean
    fun removeBlock(pos: BlockPos)
}

class TestableWorld(val level: LevelAccessor? = null, val fakeWorld: FakeWorldShim? = null) {
    fun isLog(pos: BlockPos): Boolean {
        return level?.getBlockState(pos)?.block?.isLog() ?: fakeWorld!!.isLog(pos)
    }

    fun isLeaves(pos: BlockPos): Boolean {
        return level?.getBlockState(pos)?.block?.isLeaves() ?: fakeWorld!!.isLeaves(pos)
    }

    fun removeBlock(pos: BlockPos, thing: Boolean) {
        level?.removeBlock(pos, thing)
        fakeWorld?.removeBlock(pos)
    }
}

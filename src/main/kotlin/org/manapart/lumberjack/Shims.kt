package org.manapart.lumberjack

import net.minecraft.core.BlockPos
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

interface FakeWorldShim {
    fun removeBlock(blockPos: BlockPos)
    fun getBlockState(blockPos: BlockPos): BlockState
}

class TestableWorld(val level: LevelAccessor? = null, val fakeWorld: FakeWorldShim? = null) {
    fun getBlockState(pos: BlockPos): BlockState {
        return level?.getBlockState(pos) ?: fakeWorld!!.getBlockState(pos)
    }

    fun removeBlock(pos: BlockPos, thing: Boolean) {
        level?.removeBlock(pos, thing)
        fakeWorld?.removeBlock(pos)
    }
}

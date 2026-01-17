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

    fun removeBlock(pos: BlockPos, tool: ItemStack?) {
        fakeWorld?.removeBlock(pos)
        level?.let { removeBlockFromLevel(it, pos, tool) }
    }
}

package org.manapart.lumberjack

import kotlin.math.pow
import kotlin.math.sqrt

data class BaseBlock(val localX: Int, val localZ: Int, val worldX: Int, val worldZ: Int, val isTree: Boolean) {
    var isTrunk = false
    private var isHarvestable = false

    fun setHarvestable(harvestable: Boolean){
        this.isHarvestable = harvestable
    }

    fun shouldHarvest(): Boolean {
        return isTrunk || isHarvestable
    }

    fun getDistance(other: BaseBlock): Int {
        val xDist = (worldX - other.worldX).toDouble().pow(2.0)
        val yDist = (worldZ - other.worldZ).toDouble().pow(2.0)
        return sqrt(xDist + yDist).toInt()
    }
}

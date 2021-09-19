package org.manapart.lumberjack

class BaseBlock(val localX: Int, val localZ: Int, val worldX: Int, val worldZ: Int, val isTree: Boolean) {
    var isTrunk = false
    var isHarvestable = false
        get() = if (isTrunk) {
            true
        } else field

    fun getDistance(other: BaseBlock): Int {
        return Math.sqrt(Math.pow((worldX - other.worldX).toDouble(), 2.0) + Math.pow((worldZ - other.worldZ).toDouble(), 2.0)).toInt()
    }
}
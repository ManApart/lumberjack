import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block
import org.manapart.lumberjack.FakeWorldShim
import org.manapart.lumberjack.TestableWorld

enum class FakeBlockType(val blockId: String, val gridId: Int) { AIR("air", 0), LOG("log", 1), LEAF("leaves", 2) }

fun Int.toType() = FakeBlockType.entries.firstOrNull { this == it.gridId } ?: FakeBlockType.AIR

//Grid only tracks x and y(vertical). Z is ignored
typealias X = Int
typealias Y = Int

class FakeWorld : FakeWorldShim {
    private val data = mutableMapOf<X, MutableMap<Y, FakeBlockType>>()
    override fun isLog(pos: BlockPos): Boolean {
        if (pos.z != 0) return false
        return data[pos.x]?.get(pos.y) == FakeBlockType.LOG
    }

    override fun isLeaves(pos: BlockPos): Boolean {
        if (pos.z != 0) return false
        return data[pos.x]?.get(pos.y) == FakeBlockType.LEAF
    }

    override fun removeBlock(pos: BlockPos) {
        if (pos.z != 0) return
        data[pos.x]?.let { it[pos.y] = FakeBlockType.AIR }
    }

    fun set(x: Int, y: Int, type: FakeBlockType) {
        data.putIfAbsent(x, mutableMapOf())
        data[x]?.put(y, type)
    }

    fun toGrid(): Array<IntArray> {
        val maxX = data.keys.size
        val maxY = data[0]?.size ?: 0
        return (0 until maxY).map { y ->
            (0 until maxX).map { x -> data[x]?.get(maxY-1-y)?.gridId ?: FakeBlockType.AIR.gridId }.toIntArray()
        }.toTypedArray()
    }
}

fun Array<IntArray>.toWorld(): TestableWorld {
    val fakeWorld = FakeWorld()
    val maxY = size-1
    forEachIndexed { y, row ->
        row.forEachIndexed { x, type ->
            fakeWorld.set(x, maxY-y, type.toType())
        }
    }
    return TestableWorld(fakeWorld = fakeWorld)
}

fun TestableWorld.toGrid(): Array<IntArray> {
    return (fakeWorld as FakeWorld?)?.toGrid() ?: arrayOf()
}

fun Array<IntArray>.toTestString(): String {
    return joinToString("\n") { it.joinToString() }
}

class FakeBlock : Block(Properties.of())

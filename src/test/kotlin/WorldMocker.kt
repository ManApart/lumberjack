import io.mockk.mockk
import net.minecraft.world.level.LevelAccessor

enum class FakeBlockType(val blockId: String, val gridId: Int) { AIR("air", 0), LOG("log", 1), LEAF("leaves", 2) }

//Grid only tracks x and y(vertical). Z is ignored
typealias X = Int
typealias Y = Int
typealias FakeWorld = MutableMap<X, MutableMap<Y, FakeBlockType>>


fun Array<IntArray>.toWorld(): Pair<FakeWorld, LevelAccessor> {
    val level = mockk<LevelAccessor>()
    val fakeWorld = mutableMapOf<X, MutableMap<Y, FakeBlockType>>()
    return fakeWorld to level
}

fun FakeWorld.toGrid(): Array<IntArray> {
    return arrayOf()
}

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour.Properties
import net.minecraft.world.level.block.state.BlockState
import org.manapart.lumberjack.FakeWorldShim
import org.manapart.lumberjack.TestableWorld

enum class FakeBlockType(val blockId: String, val gridId: Int) { AIR("air", 0), LOG("log", 1), LEAF("leaves", 2) }

//Grid only tracks x and y(vertical). Z is ignored
typealias X = Int
typealias Y = Int
class FakeWorld : FakeWorldShim {
    private val data = mutableMapOf<X, MutableMap<Y, FakeBlockType>>()
    override fun removeBlock(blockPos: BlockPos) {

    }

    override fun getBlockState(blockPos: BlockPos): BlockState {
//        fakeWorld!!.get(pos.x)[pos.y]
        return Block(Properties.of()).defaultBlockState()
    }
}

fun Array<IntArray>.toWorld(): TestableWorld {
    val fakeWorld = FakeWorld()

    return TestableWorld()
}

fun TestableWorld.toGrid(): Array<IntArray> {
    return arrayOf()
}

class FakeBlock : Block(Properties.of())

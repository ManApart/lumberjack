import net.fabricmc.fabric.api.networking.v1.PlayerLookup.world
import net.minecraft.core.BlockPos
import org.manapart.lumberjack.Lumberjack
import kotlin.test.Test
import kotlin.test.assertEquals

/*
0 = air
1 = tree
2 = leaf
 */
class ChoppingTest {


    @Test
    fun noLogChopped() {
        val grid = arrayOf(
            intArrayOf(2, 2, 2),
            intArrayOf(0, 1, 2),
            intArrayOf(0, 1, 0),
        )

        val actual = grid.toWorld().toGrid()

        assertEquals(grid.toTestString(), actual.toTestString())
    }

    @Test
    fun simpleTreeChop() {
        val world = arrayOf(
            intArrayOf(2, 2, 2),
            intArrayOf(0, 1, 0),
            intArrayOf(0, 1, 0),
        ).toWorld()

        val expected = arrayOf(
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
        )

        Lumberjack.fellLogs(BlockPos(1, 0, 0), world, null)
        assertEquals(expected.toTestString(), world.toGrid().toTestString())
    }

    @Test
    fun tallTree() {
        val world = arrayOf(
            intArrayOf(2, 2, 2),
            intArrayOf(2, 1, 2),
            intArrayOf(0, 1, 0),
            intArrayOf(0, 1, 0),
        ).toWorld()

        val expected = arrayOf(
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
        )

        Lumberjack.fellLogs(BlockPos(1, 0, 0), world, null)
        assertEquals(expected.toTestString(), world.toGrid().toTestString())
    }

    @Test
    fun twoTrees() {
        val world = arrayOf(
            intArrayOf(2, 2, 2, 2, 2, 2),
            intArrayOf(0, 1, 0, 0, 1, 0),
            intArrayOf(0, 1, 0, 0, 1, 0),
        ).toWorld()

        val expected = arrayOf(
            intArrayOf(0, 0, 0, 2, 2, 2),
            intArrayOf(0, 0, 0, 0, 1, 0),
            intArrayOf(0, 0, 0, 0, 1, 0),
        )

        Lumberjack.fellLogs(BlockPos(1, 0, 0), world, null)
        assertEquals(expected.toTestString(), world.toGrid().toTestString())
    }

    @Test
    fun tightTrees() {
        val world = arrayOf(
            intArrayOf(2, 2, 2, 2, 2),
            intArrayOf(0, 1, 0, 1, 0),
            intArrayOf(0, 1, 0, 1, 0),
        ).toWorld()

        val expected = arrayOf(
            intArrayOf(0, 0, 0, 2, 2, 2),
            intArrayOf(0, 0, 0, 0, 1, 0),
            intArrayOf(0, 0, 0, 0, 1, 0),
        )

        Lumberjack.fellLogs(BlockPos(1, 0, 0), world, null)
        assertEquals(expected.toTestString(), world.toGrid().toTestString())
    }

}

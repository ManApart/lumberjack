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

        val expected = airGrid(3, 3)

        Lumberjack.fellLogs(BlockPos(1, 0, 0), world, null)
        assertEquals(expected.toTestString(), world.toGrid().toTestString())
    }

    @Test
    fun chopOuterLeavesBirch() {
        val world = arrayOf(
            intArrayOf(2, 2, 2, 2, 2),
            intArrayOf(2, 2, 1, 2, 2),
            intArrayOf(2, 2, 1, 2, 2),
            intArrayOf(0, 0, 1, 0, 0),
            intArrayOf(0, 0, 1, 0, 0),
            intArrayOf(0, 0, 1, 0, 0),
        ).toWorld()

        val expected = airGrid(5, 6)

        Lumberjack.fellLogs(BlockPos(2, 0, 0), world, null)
        assertEquals(expected.toTestString(), world.toGrid().toTestString())
    }

    @Test
    fun chopBranchesOak() {
        val world = arrayOf(
            intArrayOf(2, 2, 1, 2, 2),
            intArrayOf(1, 1, 1, 1, 1),
            intArrayOf(0, 0, 1, 0, 0),
            intArrayOf(0, 0, 1, 0, 0),
        ).toWorld()

        val expected = airGrid(5, 4)

        Lumberjack.fellLogs(BlockPos(2, 0, 0), world, null)
        assertEquals(expected.toTestString(), world.toGrid().toTestString())
    }

    @Test
    fun chopHalfWayUp() {
        val world = arrayOf(
            intArrayOf(2, 2, 2),
            intArrayOf(0, 1, 0),
            intArrayOf(0, 1, 0),
        ).toWorld()

        val expected = arrayOf(
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 1, 0),
        )

        Lumberjack.fellLogs(BlockPos(1, 1, 0), world, null)
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

        val expected = airGrid(3, 4)

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
            intArrayOf(0, 0, 0, 0, 2, 2),
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
            intArrayOf(0, 0, 0, 2, 2),
            intArrayOf(0, 0, 0, 1, 0),
            intArrayOf(0, 0, 0, 1, 0),
        )

        Lumberjack.fellLogs(BlockPos(1, 0, 0), world, null)
        assertEquals(expected.toTestString(), world.toGrid().toTestString())
    }

    private fun airGrid(x: Int, y: Int): Array<IntArray> {
        return (0..<y).map { IntArray(x) }.toTypedArray()
    }

}

package org.manapart.lumberjack;

import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class ColumnFinder {
    private static final int DISTANCE = 5;
    private BlockPos source;

    public ColumnFinder(BlockPos source) {
        this.source = source;
    }

    public ArrayList<BlockPos> findColumns() {
        BlockPos[][] base = findBase();
        return null;
    }

    private BlockPos[][] findBase() {
        BlockPos[][] base = new BlockPos[2*DISTANCE][2*DISTANCE];

    }

}

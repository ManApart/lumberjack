package org.manapart.lumberjack;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

import java.util.ArrayList;
import java.util.HashSet;

public class ColumnFinder {
    private static final int DISTANCE = 5;
    private final BlockPos source;
    private final IWorld world;

    public ColumnFinder(BlockPos source, IWorld world) {
        this.source = source;
        this.world = world;
    }

    public ArrayList<BlockPos> findColumns() {
        BaseBlock[][] baseBlocks = getBaseBlocks();
        findTrunks(baseBlocks);
        return null;
    }

    private BaseBlock[][] getBaseBlocks() {
        BaseBlock[][] baseBlocks = new BaseBlock[2 * DISTANCE][2 * DISTANCE];

        for (int x = 0; x < DISTANCE; x++) {
            for (int z = 0; z < DISTANCE; z++) {
                BlockPos pos = source.add(x, 0, z);
                Block block = world.getBlockState(pos).getBlock();
                boolean isLog = Lumberjack.isLog(block);
                baseBlocks[x][z] = new BaseBlock(pos.getX(), pos.getZ(), isLog);
                if (!isLog){
                    baseBlocks[x][z].setType(ColumnType.SURROUNDING);
                    baseBlocks[x][z].setTrunk(false);
                } else {
                    //Initially all logs set to neighbor tree. Next step updates appropriate ones to trunks
                    baseBlocks[x][z].setType(ColumnType.NEIGHBOR_TREE);
                }
            }
        }
        return baseBlocks;
    }

    private void findTrunks(BaseBlock[][] baseBlocks) {
        ArrayList<BaseBlock> open = new ArrayList<>();
        HashSet<BaseBlock> closed = new HashSet<>();

        open.add(baseBlocks[DISTANCE][DISTANCE]);

        while (open.size() > 0) {
            BaseBlock current = open.get(open.size()-1);
            open.remove(current);
            if (current != null && !closed.contains(current)) {
                closed.add(current);

                if (current.isTree()) {
                    current.setTrunk(true);
                    open.add(getBaseBlock(current.getX() + 1, current.getZ(), baseBlocks));
                    open.add(getBaseBlock(current.getX() - 1, current.getZ(), baseBlocks));
                    open.add(getBaseBlock(current.getX(), current.getZ() + 1, baseBlocks));
                    open.add(getBaseBlock(current.getX(), current.getZ() - 1, baseBlocks));
                }
            }
        }
    }

    private BaseBlock getBaseBlock(int x, int z, BaseBlock[][] baseBlocks) {
        if (x < DISTANCE*2 && z < DISTANCE*2){
            return baseBlocks[x][z];
        } else {
            return null;
        }
    }

}

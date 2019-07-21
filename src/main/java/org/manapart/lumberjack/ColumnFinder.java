package org.manapart.lumberjack;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

import java.util.ArrayList;
import java.util.HashSet;

public class ColumnFinder {
    private static final int RADIUS = 5;
    private static final int DIAMETER = RADIUS * 2 + 1;
    private final BlockPos source;
    private final IWorld world;

    public ColumnFinder(BlockPos source, IWorld world) {
        this.source = source;
        this.world = world;
    }

    public ArrayList<BlockPos> findColumns() {
        BaseBlock[][] baseBlocks = getBaseBlocks();
        denoteTrunks(baseBlocks);
        denoteHarvestableBlocks(baseBlocks);
        ArrayList<BlockPos> positions = filterHarvestable(baseBlocks);

        return positions;
    }

    private BaseBlock[][] getBaseBlocks() {
        BaseBlock[][] baseBlocks = new BaseBlock[DIAMETER][DIAMETER];

        for (int x = 0; x < DIAMETER; x++) {
            for (int z = 0; z < DIAMETER; z++) {
                BlockPos pos = source.add(x - RADIUS, 0, z - RADIUS);
                Block block = world.getBlockState(pos).getBlock();
                boolean isLog = Lumberjack.isLog(block);
                baseBlocks[x][z] = new BaseBlock(x, z, pos.getX(), pos.getZ(), isLog);
                if (!isLog) {
                    baseBlocks[x][z].setTrunk(false);
                }
            }
        }
        return baseBlocks;
    }

    private void denoteTrunks(BaseBlock[][] baseBlocks) {
        ArrayList<BaseBlock> open = new ArrayList<>();
        HashSet<BaseBlock> closed = new HashSet<>();

        open.add(baseBlocks[RADIUS][RADIUS]);

        while (open.size() > 0) {
            BaseBlock current = open.get(open.size() - 1);
            open.remove(current);
            if (current != null && !closed.contains(current)) {
                closed.add(current);

                if (current.isTree()) {
                    current.setTrunk(true);
                    open.add(getBaseBlock(current.getLocalX() + 1, current.getLocalZ(), baseBlocks));
                    open.add(getBaseBlock(current.getLocalX() - 1, current.getLocalZ(), baseBlocks));
                    open.add(getBaseBlock(current.getLocalX(), current.getLocalZ() + 1, baseBlocks));
                    open.add(getBaseBlock(current.getLocalX(), current.getLocalZ() - 1, baseBlocks));
                }
            }
        }
    }

    private BaseBlock getBaseBlock(int x, int z, BaseBlock[][] baseBlocks) {
        if (x > 0 && z > 0 && x < DIAMETER && z < DIAMETER) {
            return baseBlocks[x][z];
        } else {
            return null;
        }
    }


    private void denoteHarvestableBlocks(BaseBlock[][] baseBlocks) {
        ArrayList<BaseBlock> trunks = new ArrayList<>();
        ArrayList<BaseBlock> neighbors = new ArrayList<>();
        ArrayList<BaseBlock> bufferBlocks = new ArrayList<>();

        for (int x = 0; x < DIAMETER; x++) {
            for (int z = 0; z < DIAMETER; z++) {
                BaseBlock block = baseBlocks[x][z];
                if (block.isTree()) {
                    if (block.isTrunk()) {
                        trunks.add(block);
                    } else {
                        neighbors.add(block);
                    }
                } else {
                    bufferBlocks.add(block);
                }
            }
        }

        for (BaseBlock block : bufferBlocks) {
            if (neighbors.isEmpty()) {
                block.setHarvestable(true);
            } else {
                int trunkDistance = getMinDistance(block, trunks);
                int neighborDistance = getMinDistance(block, neighbors);

                block.setHarvestable(trunkDistance <= neighborDistance);
            }
        }
    }

    private int getMinDistance(BaseBlock block, ArrayList<BaseBlock> blocks) {
        int min = DIAMETER;

        for (BaseBlock other: blocks){
            int dist = block.getDistance(other);
            if (dist < min){
                min = dist;
            }
        }
        return min;
    }


    private ArrayList<BlockPos> filterHarvestable(BaseBlock[][] baseBlocks) {
        ArrayList<BlockPos> positions = new ArrayList<>();
        for (BaseBlock[] blocks : baseBlocks) {
            for (BaseBlock block : blocks) {
                if (block.isHarvestable()) {
                    positions.add(new BlockPos(block.getWorldX(), source.getY(), block.getWorldZ()));
                }
            }
        }
        return positions;
    }

}

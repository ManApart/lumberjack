package org.manapart.lumberjack;

public class BaseBlock {
    private final int worldX;
    private final int worldZ;
    private final int localX;
    private final int localZ;
    private final boolean isTree;
    private boolean isTrunk;
    private boolean isHarvestable;

    public BaseBlock(int localX, int localZ, int worldX, int worldZ, boolean isTree) {
        this.localX = localX;
        this.localZ = localZ;
        this.worldX = worldX;
        this.worldZ = worldZ;
        this.isTree = isTree;
    }

    public int getDistance(BaseBlock other) {
        return (int) Math.sqrt(Math.pow(worldX - other.worldX, 2) + Math.pow(worldZ - other.worldZ, 2));
    }

    public int getWorldX() {
        return worldX;
    }

    public int getWorldZ() {
        return worldZ;
    }

    public boolean isTree() {
        return isTree;
    }

    public boolean isTrunk() {
        return isTrunk;
    }

    public void setTrunk(boolean trunk) {
        isTrunk = trunk;
    }

    public boolean isHarvestable() {
        if (isTrunk) {
            return true;
        }
        return isHarvestable;
    }

    public void setHarvestable(boolean harvestable) {
        isHarvestable = harvestable;
    }

    public int getLocalZ() {
        return localZ;
    }

    public int getLocalX() {
        return localX;
    }
}

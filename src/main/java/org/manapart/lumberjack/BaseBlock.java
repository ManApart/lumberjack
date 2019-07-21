package org.manapart.lumberjack;

public class BaseBlock {
    private final int x;
    private final int z;
    private final boolean isTree;
    private boolean isTrunk;
    private ColumnType type;

    public BaseBlock(int x, int z, boolean isTree) {
        this.x = x;
        this.z = z;
        this.isTree = isTree;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
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

    public void setType(ColumnType type) {
        this.type = type;
    }

    public ColumnType getType() {
        return type;
    }
}

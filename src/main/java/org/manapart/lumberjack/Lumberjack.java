package org.manapart.lumberjack;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("lumberjack")
public class Lumberjack {

    public Lumberjack() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onBreak(BlockEvent.BreakEvent event) {
        Block block = event.getState().getBlock();
        BlockPos pos = event.getPos();

        if (shouldFellTrees(event.getPlayer())) {
            if (isLog(block)) {
                System.out.println(event.getPlayer().getName() + " broke " + block + " at " + pos);
                fellLogs(pos, event.getWorld(), event.getPlayer().getHeldItemMainhand());
            }
        }
    }

    public static boolean isLog(Block block) {
        return isType(block, "log");
    }

    public static boolean isLeaves(Block block) {
        return isType(block, "leaves");
    }

    private static boolean isType(Block block, String type) {
        if (block.getRegistryName() != null) {
            return block.getRegistryName().getPath().toLowerCase().contains(type);
        }
        return false;
    }

    private boolean shouldFellTrees(PlayerEntity player) {
        Item item = player.getHeldItemMainhand().getItem();
        return !player.isSneaking() && item.getToolTypes(player.getHeldItemMainhand()).contains(ToolType.AXE);
    }


    private void fellLogs(BlockPos sourcePosition, IWorld world, ItemStack tool) {
        ColumnFinder finder = new ColumnFinder(sourcePosition, world);
        ArrayList<BlockPos> columns = finder.findColumns();
        chopColumns(columns, 1, world, tool);
    }

    private void chopColumns(ArrayList<BlockPos> columns, int y, IWorld world, ItemStack tool) {
        boolean atLeastOneBlockHarvested = false;
        for (BlockPos column : columns) {
            BlockPos pos = column.add(0, y, 0);
            BlockState blockState = world.getBlockState(pos);
            Block block = blockState.getBlock();

            if (isLog(block) || isLeaves(block)) {
                dropBlock(world, pos, blockState, tool);
                atLeastOneBlockHarvested = true;
            }
        }
        if (atLeastOneBlockHarvested) {
            chopColumns(columns, y + 1, world, tool);
        }
    }

    private void dropBlock(IWorld world, BlockPos pos, BlockState state, ItemStack tool) {
        if (world instanceof ServerWorld) {
            LootContext.Builder lootContext = new LootContext.Builder((ServerWorld) world);
            lootContext.withParameter(LootParameters.TOOL, tool);
            lootContext.withParameter(LootParameters.POSITION, pos);
            List<ItemStack> drops = state.getDrops(lootContext);
            world.removeBlock(pos, false);

            if (drops.size() >= 1) {
                dropItems(world, pos, drops);
            }
        }
    }

    private void dropItems(IWorld world, BlockPos pos, List<ItemStack> drops) {
        for (ItemStack i : drops) {
            if (i != null) {
                ItemEntity dropItem = new ItemEntity(world.getWorld(), (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, i);
                world.addEntity(dropItem);
            }
        }
    }


}

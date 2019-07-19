package org.manapart.lumberjack;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSet;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

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

    private boolean isLog(Block block) {
        return isType(block, "log");
    }

    private boolean isLeaves(Block block) {
        return isType(block, "leaves");
    }

    private boolean isType(Block block, String type) {
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
        ArrayList<BlockPos> open = new ArrayList<>();
        ArrayList<BlockPos> closed = new ArrayList<>();

        open.add(sourcePosition);

        while (open.size() > 0) {
            BlockPos currentPosition = open.get(open.size() - 1);
            open.remove(currentPosition);
            if (!closed.contains(currentPosition)) {
                closed.add(currentPosition);

                BlockState currentState = world.getBlockState(currentPosition);
                open.addAll(getNeighbors(currentPosition, world));
                dropBlock(world, currentPosition, currentState, tool);
            }
        }
    }

    private ArrayList<BlockPos> getNeighbors(BlockPos position, IWorld world) {
        ArrayList<BlockPos> neighbors = new ArrayList<>();

        BlockPos up = position.up();
        addLogsAndLeaves(neighbors, world, up);
        addLogsAndLeaves(neighbors, world, up.north());
        addLogsAndLeaves(neighbors, world, up.south());
        addLogsAndLeaves(neighbors, world, up.west());
        addLogsAndLeaves(neighbors, world, up.east());

        return neighbors;
    }

    private void addLogsAndLeaves(ArrayList<BlockPos> neighbors, IWorld world, BlockPos position) {
        BlockState state = world.getBlockState(position);
        Block currentBlock = state.getBlock();
        if (isLog(currentBlock) || isLeaves(currentBlock)) {
            neighbors.add(position);
        }
    }

    private void dropBlock(IWorld world, BlockPos pos, BlockState state, ItemStack tool) {
        if (world instanceof ServerWorld) {
            LootContext.Builder lootContext = new LootContext.Builder((ServerWorld) world);
            lootContext.withParameter(LootParameters.TOOL, tool);
            lootContext.withParameter(LootParameters.POSITION, pos);
            List<ItemStack> drops = state.getDrops(lootContext);
            world.removeBlock(pos, false);

            if (shouldDropItems(drops)) {
                dropItems(world, pos, drops);
            }
        }
    }

    private boolean shouldDropItems(List<ItemStack> drops) {
        return drops.size() >= 1;
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

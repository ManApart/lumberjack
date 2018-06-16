package org.iceburg.lumberjack;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


public class TreeChopEventHandler {
	
	@SubscribeEvent
	public void onSwing(BreakEvent event) {
		Block block = event.getState().getBlock();
		World world = event.getWorld();
		BlockPos pos = event.getPos();
		
		if (shouldFellTrees(event)){
			if (block.isWood(world, pos)){
				System.out.println(event.getPlayer().getName() + " broke " + block.getLocalizedName() + " at " + pos);
				
				fellLogs((BlockLog) block, pos, world);
			}
		}
		
	}

	private boolean shouldFellTrees(BreakEvent event) {
		Item item = event.getPlayer().getHeldItemMainhand().getItem();
		return !event.getPlayer().isSneaking() && item instanceof ItemAxe;
	}

	private void fellLogs(BlockLog source, BlockPos sourcePosition, World world) {
		ArrayList<BlockPos> open = new ArrayList<>();
		ArrayList<BlockPos> closed = new ArrayList<>();
		
		open.add(sourcePosition);
		
		while (open.size() > 0){
			BlockPos currentPosition = open.get(open.size()-1);
			open.remove(currentPosition);
			if (!closed.contains(currentPosition)){
				closed.add(currentPosition);
				
				IBlockState currentState = world.getBlockState(currentPosition);
				open.addAll(getNeighbors(currentPosition, world));
				
//				Block currentBlock = world.getBlockState(currentPosition).getBlock();
//				System.out.println("Breaking " + currentBlock.getLocalizedName() + " at " + currentPosition);
				dropBlock(world, currentPosition, currentState);
			}
		}
	}

	private ArrayList<BlockPos> getNeighbors(BlockPos position, World world) {
		ArrayList<BlockPos> neighbors = new ArrayList<>();
		
		BlockPos up = position.up();
		addLogsAndLeaves(neighbors, world, up);
		addLogsAndLeaves(neighbors, world, up.north());
		addLogsAndLeaves(neighbors, world, up.south());
		addLogsAndLeaves(neighbors, world, up.west());
		addLogsAndLeaves(neighbors, world, up.east());
		
		return neighbors;
	}

	private void addLogsAndLeaves(ArrayList<BlockPos> neighbors, World world, BlockPos position) {
		IBlockState state = world.getBlockState(position);
		Block currentBlock = state.getBlock();
		if (currentBlock.isWood(world, position) || currentBlock.isLeaves(state, world, position)){
			neighbors.add(position);
		}
	}

	public void dropBlock(World world, BlockPos pos, IBlockState state){
		List<ItemStack> drops = state.getBlock().getDrops(world, pos, state, 0);
		world.setBlockToAir(pos);
		
		if(shouldDropItems(world, drops)) { 
			dropItems(world, pos, drops);
		}
	}

	private boolean shouldDropItems(World world, List<ItemStack> drops) {
		return drops.size() >= 1 && isServer(world);
	}

	private boolean isServer(World world) {
		return !world.isRemote;
	}
	
	private void dropItems(World world, BlockPos pos, List<ItemStack> drops) {
		for(ItemStack i : drops){
			if(i != null && i.getItem() != null){
				EntityItem dropItem = new EntityItem(world, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, i);
				world.spawnEntity(dropItem);
			}
		}
	}

		

}

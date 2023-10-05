package com.quarrel.glasspole.block;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluids;


// Code mostly copied from BubbleColumnBlock.

public class SulfurBubbleColumnBlock extends BubbleColumnBlock {

	private static final int CHECK_PERIOD = 5;

	public SulfurBubbleColumnBlock(Properties properties) {
        super(properties);
    }

	
    // @Override
    public static void updateColumn(LevelAccessor pLevel, BlockPos pPos, BlockState pState) {
        updateColumn(pLevel, pPos, pLevel.getBlockState(pPos), pState);
    }

    // @Override
    public static void updateColumn(LevelAccessor pLevel, BlockPos pPos, BlockState pState, BlockState pOtherState) {
        if (canExistIn(pState)) {
        	BlockState blockstate = getColumnState(pOtherState);
        	pLevel.setBlock(pPos, blockstate, UPDATE_CLIENTS);
        	BlockPos.MutableBlockPos blockpos$mutableblockpos = pPos.mutable().move(Direction.UP);
        	while(canExistIn(pLevel.getBlockState(blockpos$mutableblockpos))) {
        		if (!pLevel.setBlock(blockpos$mutableblockpos, blockstate, UPDATE_CLIENTS)) {
        			return;
        		}
        		blockpos$mutableblockpos.move(Direction.UP);
        	}
        }
     }

    // @Override
    private static boolean canExistIn(BlockState pState) {
        return pState.is(Blocks.BUBBLE_COLUMN) || pState.is(ModBlocks.SULFUR_BUBBLE_COLUMN.get())
        		|| pState.is(Blocks.WATER) && pState.getFluidState().getAmount() >= 8 && pState.getFluidState().isSource();
     }

    // @Override
    private static BlockState getColumnState(BlockState pState) {
        if (pState.is(ModBlocks.SULFUR_BUBBLE_COLUMN.get())) {
           return pState;
        } else if (pState.is(Blocks.BUBBLE_COLUMN)) {
        	Boolean dd = pState.getValue(DRAG_DOWN);
        	return ModBlocks.SULFUR_BUBBLE_COLUMN.get().defaultBlockState().setValue(DRAG_DOWN, Boolean.valueOf(dd));
        } else if (pState.is(Blocks.SOUL_SAND)) {
           return ModBlocks.SULFUR_BUBBLE_COLUMN.get().defaultBlockState().setValue(DRAG_DOWN, Boolean.valueOf(false));
        } else if (pState.is(Blocks.MAGMA_BLOCK) || pState.is(ModBlocks.SULFUR_MAGMA_BLOCK.get())) {
           return ModBlocks.SULFUR_BUBBLE_COLUMN.get().defaultBlockState().setValue(DRAG_DOWN, Boolean.valueOf(true));
        }
        return Blocks.WATER.defaultBlockState();
     }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pOtherState, LevelAccessor pLevel, BlockPos pPos, BlockPos pOtherPos) {
       pLevel.scheduleTick(pPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
       if (!pState.canSurvive(pLevel, pPos) || pDirection == Direction.DOWN ||
    		   (pDirection == Direction.UP && !pOtherState.is(ModBlocks.SULFUR_BUBBLE_COLUMN.get()) && canExistIn(pOtherState))) {
          pLevel.scheduleTick(pPos, this, CHECK_PERIOD);
       }

       return super.updateShape(pState, pDirection, pOtherState, pLevel, pPos, pOtherPos);
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockState stateBelow = pLevel.getBlockState(pPos.below());
        return stateBelow.is(Blocks.BUBBLE_COLUMN) || stateBelow.is(Blocks.MAGMA_BLOCK) || stateBelow.is(Blocks.SOUL_SAND)
        		|| stateBelow.is(ModBlocks.SULFUR_BUBBLE_COLUMN.get()) || stateBelow.is(ModBlocks.SULFUR_MAGMA_BLOCK.get());
     }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, Random pRand) {
        updateColumn(pLevel, pPos, pState, pLevel.getBlockState(pPos.below()));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_50997_) {
       p_50997_.add(DRAG_DOWN);
    }
}

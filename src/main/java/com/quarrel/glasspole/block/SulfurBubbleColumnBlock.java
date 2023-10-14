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
    public static void updateColumn(LevelAccessor pLevel, BlockPos pPos, BlockState pState, BlockState pStateBelow) {
        if (canExistIn(pState)) {
        	BlockState newOtherState = getNewColumnState(pStateBelow);
        	pLevel.setBlock(pPos, newOtherState, UPDATE_CLIENTS);
        	BlockPos.MutableBlockPos blockpos$mutableblockpos = pPos.mutable().move(Direction.UP);
        	while(canExistIn(pLevel.getBlockState(blockpos$mutableblockpos))) {
        		if (!pLevel.setBlock(blockpos$mutableblockpos, newOtherState, UPDATE_CLIENTS)) {
        			return;
        		}
        		blockpos$mutableblockpos.move(Direction.UP);
        	}
        }
     }

    // @Override
    private static boolean canExistIn(BlockState pState) {
        return pState.is(Blocks.BUBBLE_COLUMN) || pState.is(ModBlocks.SULFUR_BUBBLE_COLUMN.get()) ||
        		(pState.is(Blocks.WATER) && pState.getFluidState().isSource() && pState.getFluidState().getAmount() >= 8);
     }

    // @Override
    private static BlockState getNewColumnState(BlockState pStateBelow) {
        if (pStateBelow.is(ModBlocks.SULFUR_BUBBLE_COLUMN.get())) {
           return pStateBelow;
        } else if (pStateBelow.is(Blocks.BUBBLE_COLUMN)) {
            return pStateBelow;
        } else if (pStateBelow.is(Blocks.SOUL_SAND)) {
           return Blocks.BUBBLE_COLUMN.defaultBlockState().setValue(DRAG_DOWN, Boolean.valueOf(false));
        } else if (pStateBelow.is(Blocks.MAGMA_BLOCK)) {
            return Blocks.BUBBLE_COLUMN.defaultBlockState().setValue(DRAG_DOWN, Boolean.valueOf(true));
        } else if (pStateBelow.is(ModBlocks.SULFUR_MAGMA_BLOCK.get())) {
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
        return stateBelow.is(ModBlocks.SULFUR_BUBBLE_COLUMN.get()) || stateBelow.is(ModBlocks.SULFUR_MAGMA_BLOCK.get());
     }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, Random pRand) {
        updateColumn(pLevel, pPos, pState, pLevel.getBlockState(pPos.below()));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
       pBuilder.add(DRAG_DOWN);
    }

/*
    @Override
    public void animateTick(BlockState p_50981_, Level p_50982_, BlockPos p_50983_, Random p_50984_) {

        double d0 = (double)p_50983_.getX();
        double d1 = (double)p_50983_.getY();
        double d2 = (double)p_50983_.getZ();
        p_50982_.addAlwaysVisibleParticle(ParticleTypes.SMALL_FLAME, d0 + 0.5D, d1 + 0.8D, d2 + 0.5D, 0.0D, 0.0D, 0.0D);
 
    	super.animateTick(p_50981_, p_50982_, p_50983_, p_50984_);
	}
*/
}

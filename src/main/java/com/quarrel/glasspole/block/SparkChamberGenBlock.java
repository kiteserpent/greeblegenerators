package com.quarrel.glasspole.block;

import java.util.Random;

import javax.annotation.Nullable;

import com.quarrel.glasspole.block.entity.SparkChamberGenBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SparkChamberGenBlock extends BaseEntityBlock {
    public static final BooleanProperty SPARKING = BooleanProperty.create("sparking");

    public SparkChamberGenBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(SPARKING, Boolean.valueOf(false)));
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
       return this.defaultBlockState().setValue(SPARKING, Boolean.valueOf(false));
    }

    @Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		pBuilder.add(SPARKING);
	}

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 16, 16);

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
    	return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
    	return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    /* BLOCK ENTITY */

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new SparkChamberGenBlockEntity(pPos, pState);
    }

    // We can't check the light level of an opaque block since it's always 0,
    // so check all its neighbors.
    private boolean inDark(Level pLevel, BlockPos pBlockPos) {
    	final int skyDarken = pLevel.getSkyDarken();
        for (Direction direction : Direction.values()) {
        	if (pLevel.getBrightness(LightLayer.BLOCK, pBlockPos.relative(direction)) > 0)
    			return false;
        	if (pLevel.getBrightness(LightLayer.SKY, pBlockPos.relative(direction)) > skyDarken)
    			return false;
        }
        return true;
    }
    
    @Override
	public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pBlockPos, Random pRandom) {
    	if (pState.getValue(SPARKING)) {
			pLevel.setBlock(pBlockPos, pState.setValue(SPARKING, Boolean.valueOf(false)), UPDATE_ALL);
			return;
    	}
    	if (!inDark(pLevel, pBlockPos))
    		return;
		if (!pLevel.isClientSide()) {
		    BlockEntity be = pLevel.getBlockEntity(pBlockPos);
		    if (be instanceof SparkChamberGenBlockEntity) {
		        ((SparkChamberGenBlockEntity) be).doRandomTick();
		    }
		}
		pLevel.setBlock(pBlockPos, pState.setValue(SPARKING, Boolean.valueOf(true)), UPDATE_ALL);
		pLevel.scheduleTick(pBlockPos, this, 4);
		pLevel.levelEvent(UPDATE_ALL_IMMEDIATE, pBlockPos, UPDATE_ALL);
	}

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide()) {
            return null;
        }
        return (lvl, pos, blockState, t) -> {
            if (t instanceof SparkChamberGenBlockEntity be) {
                be.tickServer(lvl, pos, blockState, be);
            }
        };
    }

}
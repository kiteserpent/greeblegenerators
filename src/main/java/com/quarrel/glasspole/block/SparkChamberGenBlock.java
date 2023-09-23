package com.quarrel.glasspole.block;

import java.util.Random;

import javax.annotation.Nullable;

import com.quarrel.glasspole.block.entity.SparkChamberGenBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SparkChamberGenBlock extends BaseEntityBlock {

    public SparkChamberGenBlock(Properties properties) {
        super(properties);
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

	@Override
	public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pBlockPos, Random pRandom) {
	    BlockEntity be = pLevel.getBlockEntity(pBlockPos);
	    if (be instanceof SparkChamberGenBlockEntity) {
	        ((SparkChamberGenBlockEntity) be).doRandomTick();
	    }
	}
}
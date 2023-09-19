package com.quarrel.glasspole.block;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GreebleCageBlock extends Block {
	
 	public static final BooleanProperty FULL = BooleanProperty.create("full");
//	public static final BooleanProperty FULL = GreebleCageBlock.FULL;
	
    public GreebleCageBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FULL, Boolean.valueOf(false)));
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext p_55659_) {
       return this.defaultBlockState().setValue(FULL, Boolean.valueOf(false));
    }

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		pBuilder.add(FULL);
	}

	@Override
	public void tick(BlockState pState, ServerLevel pLevel, BlockPos pBlockPos, Random pRandom) {
		if (!pLevel.isClientSide()) {
			// pLevel.setBlock(pBlockPos, pState.setValue(FULL, Boolean.valueOf(true)), UPDATE_ALL);
		}
		// super.randomTick(pState, pLevel, pBlockPos, pRandom);
	}

    
    private static final VoxelShape SHAPE = Block.box(2, 0, 4, 14, 8, 12);

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

}
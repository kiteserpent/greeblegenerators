package net.quarrel.greeblegens.block;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GreebleCageBlock extends Block {
	
 	public static final BooleanProperty FULL = BooleanProperty.create("full");
	
    public GreebleCageBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FULL, Boolean.valueOf(false)));
    }

    @Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		pBuilder.add(FULL);
	}

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext p_55659_) {
       return this.defaultBlockState().setValue(FULL, Boolean.valueOf(false));
    }

    public boolean canSurvive(BlockState pBlockState, LevelReader pLevelReader, BlockPos pBlockPos) {
    	return pLevelReader.getBlockState(pBlockPos.below()).is(Blocks.GRASS_BLOCK);
    }

    // copied from vanilla CarpetBlock
    @SuppressWarnings("deprecation")
	@Override
    public BlockState updateShape(BlockState state, Direction side, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos)
    {
        return !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, side, neighborState, level, pos, neighborPos);
    }

	@Override
	public void tick(BlockState pState, ServerLevel pLevel, BlockPos pBlockPos, Random pRandom) {
		if (!pLevel.isClientSide() && !pState.getValue(FULL)) {
			if (!pLevel.hasNearbyAlivePlayer((double)pBlockPos.getX() + 0.5, (double)pBlockPos.getY() + 0.5, (double)pBlockPos.getZ() + 0.5, 24.0)) {
				pLevel.setBlock(pBlockPos, pState.setValue(FULL, Boolean.valueOf(true)), UPDATE_ALL);
			}
		}
	}
    
    private static final VoxelShape SHAPE = Block.box(2, 0, 4, 13, 8, 12);

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
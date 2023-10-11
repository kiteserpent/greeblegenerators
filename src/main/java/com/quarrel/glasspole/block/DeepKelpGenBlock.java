package com.quarrel.glasspole.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

import com.quarrel.glasspole.GlassPole;
import com.quarrel.glasspole.block.entity.DeepKelpGenBlockEntity;

public class DeepKelpGenBlock extends BaseEntityBlock {

    public DeepKelpGenBlock(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    /* BLOCK ENTITY */

    @SuppressWarnings("deprecation")
	@Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
    	if (pState.getBlock() != pNewState.getBlock()) {
    	    BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
    	    if (blockEntity instanceof DeepKelpGenBlockEntity) {
    	        ((DeepKelpGenBlockEntity) blockEntity).drops();
    	    }
    	}
    	super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

/*
 * Not bothering with re-checking depth on local update.
 * 
    @SuppressWarnings("deprecation")
	@Override
    public void neighborChanged(BlockState pNewState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pPos2, boolean dynamicShape) {
	    BlockEntity ourBE = pLevel.getBlockEntity(pPos);
	    if (ourBE instanceof DeepKelpGenBlockEntity) {
	        ((DeepKelpGenBlockEntity) ourBE).checkDepth();
	    }
        super.neighborChanged(pNewState, pLevel, pPos, pBlock, pPos2, dynamicShape);
    }
*/

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos,
                                 Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if (entity instanceof DeepKelpGenBlockEntity) {
                NetworkHooks.openGui(((ServerPlayer)pPlayer), (DeepKelpGenBlockEntity)entity, pPos);
            } else {
                throw new IllegalStateException("Our Container provider is missing for Deep Kelp Generator!");
            }
        }

        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
    	return new DeepKelpGenBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide()) {
            return null;
        }
        return (lvl, pos, blockState, t) -> {
            if (t instanceof DeepKelpGenBlockEntity be) {
                be.tickServer(lvl, pos, blockState, be);
            }
        };
    }

}

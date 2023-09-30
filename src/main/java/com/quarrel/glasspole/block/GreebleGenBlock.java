package com.quarrel.glasspole.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

import com.quarrel.glasspole.block.entity.GreebleGenBlockEntity;

public class GreebleGenBlock extends BaseEntityBlock {

    public GreebleGenBlock(Properties properties) {
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
    	    if (blockEntity instanceof GreebleGenBlockEntity) {
    	        ((GreebleGenBlockEntity) blockEntity).drops();
    	    }
    	}
    	super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos,
                                 Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if (entity instanceof GreebleGenBlockEntity) {
                NetworkHooks.openGui(((ServerPlayer)pPlayer), (GreebleGenBlockEntity)entity, pPos);
            } else {
                throw new IllegalStateException("Our Container provider is missing for Greeble Generator!");
            }
        }

        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new GreebleGenBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide()) {
            return null;
        }
        return (lvl, pos, blockState, t) -> {
            if (t instanceof GreebleGenBlockEntity be) {
                be.tickServer(lvl, pos, blockState, be);
            }
        };
    }

}

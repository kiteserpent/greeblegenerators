package com.quarrel.glasspole.block;

import javax.annotation.Nullable;

import com.quarrel.glasspole.block.entity.ModBlockEntities;
import com.quarrel.glasspole.block.entity.StaticGlassGenBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StaticGlassGenBlock extends BaseEntityBlock {

    public StaticGlassGenBlock(Properties properties) {
        super(properties);
    }

    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 13, 14);

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


    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        /*
    	if (pState.getBlock() != pNewState.getBlock()) {
    	    BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
    	    if (blockEntity instanceof StaticGlassGenBlockEntity) {
    	        ((StaticGlassGenBlockEntity) blockEntity).drops();
    	    }
    	}
    	*/
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new StaticGlassGenBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (!pLevel.isClientSide()) {
            return (lvl, pos, stt, te) -> {
                if (te instanceof StaticGlassGenBlockEntity generator) generator.tick();
            };
        }
        return null;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof StaticGlassGenBlockEntity) {
            	ItemStack stack = player.getItemInHand(hand);
        		if (!stack.isEmpty() && stack.is(ItemTags.WOOL)) {
        			((StaticGlassGenBlockEntity)be).rub();
	            }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());    }
}
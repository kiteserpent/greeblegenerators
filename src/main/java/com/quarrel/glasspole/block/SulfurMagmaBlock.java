package com.quarrel.glasspole.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.MagmaBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

import com.mojang.logging.LogUtils;
import com.quarrel.glasspole.GlassPole;

public class SulfurMagmaBlock extends MagmaBlock {
	
   public static boolean always(BlockState p_50775_, BlockGetter p_50776_, BlockPos p_50777_) {
	      return true;
	   }

    public SulfurMagmaBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pBlockPos, Random p_54809_) {
    	SulfurBubbleColumnBlock.updateColumn(pLevel, pBlockPos.above(), pState);
    }

    @Override
    public BlockState updateShape(BlockState p_54811_, Direction p_54812_, BlockState p_54813_, LevelAccessor p_54814_, BlockPos p_54815_, BlockPos p_54816_) {
        return super.updateShape(p_54811_, p_54812_, p_54813_, p_54814_, p_54815_, p_54816_);
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pBlockPos, Random pRand) {
    	if (pRand.nextFloat() < 0.125f) {
    		pLevel.setBlockAndUpdate(pBlockPos, Blocks.MAGMA_BLOCK.defaultBlockState());
    	} else {
	        BlockPos blockpos = pBlockPos.above();
	        if (pLevel.getFluidState(pBlockPos).is(FluidTags.WATER)) {
	           pLevel.playSound((Player)null, pBlockPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (pLevel.random.nextFloat() - pLevel.random.nextFloat()) * 0.8F);
	           pLevel.sendParticles(ParticleTypes.LARGE_SMOKE, (double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.25D, (double)blockpos.getZ() + 0.5D, 8, 0.5D, 0.25D, 0.5D, 0.0D);
	        }
    	}
     }
}
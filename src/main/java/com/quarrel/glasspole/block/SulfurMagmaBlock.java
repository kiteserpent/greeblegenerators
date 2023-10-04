package com.quarrel.glasspole.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
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

    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pBlockPos, Random p_54809_) {
    	if (pLevel.isClientSide())
    		GlassPole.LOGGER.info("in tick Client");
    	else
    		GlassPole.LOGGER.info("in tick nonclient");
    	BubbleColumnBlock.updateColumn(pState, pLevel.above(), pBlockPos);
    }

    public void randomTick(BlockState p_54818_, ServerLevel p_54819_, BlockPos p_54820_, Random p_54821_) {
    	if (p_54819_.isClientSide())
    		GlassPole.LOGGER.info("in randomTick Client");
    	else
        	GlassPole.LOGGER.info("in randomTick nonclient");
        BlockPos blockpos = p_54820_.above();
        if (p_54819_.getFluidState(p_54820_).is(FluidTags.WATER)) {
           p_54819_.playSound((Player)null, p_54820_, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (p_54819_.random.nextFloat() - p_54819_.random.nextFloat()) * 0.8F);
           p_54819_.sendParticles(ParticleTypes.LARGE_SMOKE, (double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.25D, (double)blockpos.getZ() + 0.5D, 8, 0.5D, 0.25D, 0.5D, 0.0D);
        }

     }
}
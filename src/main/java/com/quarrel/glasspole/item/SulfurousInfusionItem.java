package com.quarrel.glasspole.item;

import com.quarrel.glasspole.block.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;

public class SulfurousInfusionItem extends Item {

    public SulfurousInfusionItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getLevel().isClientSide()) {
            BlockPos positionClicked = pContext.getClickedPos();
            Player player = pContext.getPlayer();
            if (pContext.getLevel().getBlockState(positionClicked).is(Blocks.MAGMA_BLOCK)) {
		        pContext.getItemInHand().shrink(1);
		        pContext.getLevel().setBlockAndUpdate(positionClicked, ModBlocks.SULFUR_MAGMA_BLOCK.get().defaultBlockState(), UPDATE_ALL);
            }
		
        }
	    return super.useOn(pContext);
    }

}

package net.quarrel.greeblegens.item;

import net.quarrel.greeblegens.block.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class SulfurousInfusionItem extends Item {

    public SulfurousInfusionItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        BlockPos positionClicked = pContext.getClickedPos();
        if (pContext.getLevel().getBlockState(positionClicked).is(Blocks.MAGMA_BLOCK)) {
	        pContext.getItemInHand().shrink(1);
	        pContext.getLevel().setBlockAndUpdate(positionClicked, ModBlocks.SULFUR_MAGMA_BLOCK.get().defaultBlockState());
		    return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    public static boolean dispenseOn(ItemStack pItemStack, Level pLevel, BlockPos pBlockPos) {
    	if (!(pLevel instanceof net.minecraft.server.level.ServerLevel))
    		return false;
    	if (!(pLevel.getBlockState(pBlockPos).is(Blocks.MAGMA_BLOCK)))
    		return false;
		pItemStack.shrink(1);
        pLevel.setBlockAndUpdate(pBlockPos, ModBlocks.SULFUR_MAGMA_BLOCK.get().defaultBlockState());
        return true;
    }
}

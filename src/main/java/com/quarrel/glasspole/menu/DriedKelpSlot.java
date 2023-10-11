package com.quarrel.glasspole.menu;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class DriedKelpSlot extends SlotItemHandler {

    public DriedKelpSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return stack.is(Items.DRIED_KELP) || stack.is(Items.DRIED_KELP_BLOCK);
	}

}
package com.quarrel.glasspole.menu;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class FussyFoodSlot extends SlotItemHandler {

    public FussyFoodSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean mayPlace(ItemStack stack) {
        FoodProperties food = stack.getItem().getFoodProperties();
        if (food == null)
        	return false;
        int foodNut = food.getNutrition();
        int foodSat = (int)((float)foodNut * 2.0f * Math.min(3.0f, food.getSaturationModifier()) + 0.5f);
        if ((foodNut == foodSat) || (foodNut == 0) || (food.getEffects().size() > 0))
        	return false;
        return true;
	}
}

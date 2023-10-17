package net.quarrel.greeblegens.menu;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class FussyFoodSlot extends SlotItemHandler {

    public FussyFoodSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
//        FoodProperties food = stack.getItem().getFoodProperties();
        FoodProperties food = stack.getItem().getFoodProperties(stack, null);
        if (food == null)
        	return false;
        int foodNut = food.getNutrition();
        int foodSat = (int)((float)foodNut * 2.0f * Math.min(1.5f, food.getSaturationModifier()) + 0.5f);
        if ((foodNut == foodSat) || (foodSat == 0) || (food.getEffects().size() > 0))
        	return false;
        return true;
	}
}

package net.quarrel.greeblegens.item;

import net.quarrel.greeblegens.block.ModBlocks;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModCreativeModeTab {

	public static final CreativeModeTab GREEBLE_TAB = new CreativeModeTab("greebletab") {
		@Override
		public ItemStack makeIcon() {
//			return new ItemStack(ModItems.CAGED_GREEBLE_ITEM.get());
			return new ItemStack(ModBlocks.GREEBLE_CAGE_BLOCK.get().asItem());
		}
	};

}

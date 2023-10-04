package com.quarrel.glasspole.item;

import com.quarrel.glasspole.GlassPole;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GlassPole.MODID);

    public static final RegistryObject<Item> CAGED_GREEBLE_ITEM = ITEMS.register("caged_greeble",
    		() -> new Item(new Item.Properties().stacksTo(1).tab(ModCreativeModeTab.GREEBLE_TAB)));
    public static final RegistryObject<Item> EXCITATION_FRAME_ITEM = ITEMS.register("excitation_frame",
    		() -> new Item(new Item.Properties().tab(ModCreativeModeTab.GREEBLE_TAB)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
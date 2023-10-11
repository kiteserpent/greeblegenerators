package com.quarrel.glasspole.menu;

import com.quarrel.glasspole.GlassPole;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {

	public static final DeferredRegister<MenuType<?>> MENUS =
		DeferredRegister.create(ForgeRegistries.CONTAINERS, GlassPole.MODID);

	private static <T extends AbstractContainerMenu>RegistryObject<MenuType<T>> registerMenuType(IContainerFactory<T> factory, String name) {
    	return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static final RegistryObject<MenuType<GreebleGenMenu>> GREEBLE_GENERATOR_MENU =
		registerMenuType(GreebleGenMenu::new, "greeble_generator_menu");
    public static final RegistryObject<MenuType<DeepKelpGenMenu>> DEEPKELP_GENERATOR_MENU =
		registerMenuType(DeepKelpGenMenu::new, "deepkelp_generator_menu");

    public static void register(IEventBus eventBus) {
    	MENUS.register(eventBus);
	}
		
}

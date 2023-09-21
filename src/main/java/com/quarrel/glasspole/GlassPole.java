package com.quarrel.glasspole;

import com.mojang.logging.LogUtils;
import com.quarrel.glasspole.block.ModBlocks;
import com.quarrel.glasspole.block.entity.ModBlockEntities;
import com.quarrel.glasspole.item.ModItems;
import com.quarrel.glasspole.menu.GreebleGenScreen;
import com.quarrel.glasspole.menu.ModMenuTypes;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("glasspole")
public class GlassPole
{
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "glasspole";

    public GlassPole()
    {
    	IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

    	ModItems.register(eventBus);
        ModBlocks.register(eventBus);
        ModBlockEntities.register(eventBus);
        ModMenuTypes.register(eventBus);
        
        // Register the setup methods for modloading
    	eventBus.addListener(this::setup);
    	eventBus.addListener(this::clientSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    private void clientSetup(final FMLClientSetupEvent event)
    {
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.STATIC_GLASS_GEN_BLOCK.get(),
        		RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.GREEBLE_CAGE_BLOCK.get(),
				RenderType.cutout());
        MenuScreens.register(ModMenuTypes.GREEBLE_GENERATOR_MENU.get(), GreebleGenScreen::new);
        }

}

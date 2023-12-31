package net.quarrel.greeblegens;

import com.mojang.logging.LogUtils;
import net.quarrel.greeblegens.block.ModBlocks;
import net.quarrel.greeblegens.block.entity.ModBlockEntities;
import net.quarrel.greeblegens.item.ModItems;
import net.quarrel.greeblegens.item.SulfurousInfusionItem;
import net.quarrel.greeblegens.menu.DeepKelpGenScreen;
import net.quarrel.greeblegens.menu.GreebleGenScreen;
import net.quarrel.greeblegens.menu.ModMenuTypes;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("greeblegenerators")
public class GreebleGens
{
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "greeblegenerators";

    public GreebleGens()
    {
    	IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

    	ModItems.register(eventBus);
        ModBlocks.register(eventBus);
        ModBlockEntities.register(eventBus);
        ModMenuTypes.register(eventBus);
        
        // Register the setup methods for modloading
    	eventBus.addListener(this::setup);
    	eventBus.addListener(this::clientSetup);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfigs.SPEC, "greeblegens-common.toml");

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

    }
        
    private void setup(final FMLCommonSetupEvent event)
    {
        DispenserBlock.registerBehavior(ModItems.SULFUROUS_INFUSION_ITEM.get(),
        		new OptionalDispenseItemBehavior() {
            protected ItemStack execute(BlockSource pBlockSource, ItemStack pItemStack) {
               this.setSuccess(true);
               Level level = pBlockSource.getLevel();
               BlockPos blockpos = pBlockSource.getPos().relative(pBlockSource.getBlockState().getValue(DispenserBlock.FACING));
               if (!SulfurousInfusionItem.dispenseOn(pItemStack, level, blockpos)) {
                  this.setSuccess(false);
               }
               return pItemStack;
            }
         });
    }

    private void clientSetup(final FMLClientSetupEvent event)
    {
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.STATIC_GLASS_GEN_BLOCK.get(),
        		RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.GREEBLE_CAGE_BLOCK.get(),
				RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.SPARK_CHAMBER_GEN_BLOCK.get(),
				RenderType.cutout());
        MenuScreens.register(ModMenuTypes.GREEBLE_GENERATOR_MENU.get(), GreebleGenScreen::new);
        MenuScreens.register(ModMenuTypes.DEEPKELP_GENERATOR_MENU.get(), DeepKelpGenScreen::new);
    }

}

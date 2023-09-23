package com.quarrel.glasspole.block;

import java.util.function.Supplier;

import com.quarrel.glasspole.GlassPole;
import com.quarrel.glasspole.item.ModItems;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {

	public static final DeferredRegister<Block> BLOCKS =
			DeferredRegister.create(ForgeRegistries.BLOCKS, GlassPole.MODID);
	
	public static void register(IEventBus eventBus) {
		BLOCKS.register(eventBus);
	}
	
	// helper methods
    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, CreativeModeTab tab) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn, tab);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block,
                                                                            CreativeModeTab tab) {
        // some preinit code
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(),
                new Item.Properties().tab(tab)));
    }
    

    public static final RegistryObject<StaticGlassGenBlock> STATIC_GLASS_GEN_BLOCK = registerBlock("static_glass_generator",
            () -> new StaticGlassGenBlock(BlockBehaviour.Properties.of(Material.STONE).strength(1.0f).sound(SoundType.GLASS).noOcclusion()), CreativeModeTab.TAB_MISC);

    public static final RegistryObject<GreebleCageBlock> GREEBLE_CAGE_BLOCK = registerBlock("greeble_cage",
            () -> new GreebleCageBlock(BlockBehaviour.Properties.of(Material.WOOD).noOcclusion().randomTicks()), CreativeModeTab.TAB_MISC);

    public static final RegistryObject<GreebleGenBlock> GREEBLE_GEN_BLOCK = registerBlock("greeble_generator",
            () -> new GreebleGenBlock(BlockBehaviour.Properties.of(Material.METAL).strength(2.0f)), CreativeModeTab.TAB_MISC);

    public static final RegistryObject<SparkChamberGenBlock> SPARK_CHAMBER_GEN_BLOCK = registerBlock("spark_chamber_generator",
            () -> new SparkChamberGenBlock(BlockBehaviour.Properties.of(Material.METAL).strength(1.0f).randomTicks().sound(SoundType.GLASS).noOcclusion()), CreativeModeTab.TAB_MISC);

    
}

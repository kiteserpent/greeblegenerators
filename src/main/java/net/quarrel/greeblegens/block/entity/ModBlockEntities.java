package net.quarrel.greeblegens.block.entity;

import net.quarrel.greeblegens.GreebleGens;
import net.quarrel.greeblegens.block.ModBlocks;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, GreebleGens.MODID);

    
    public static final RegistryObject<BlockEntityType<StaticGlassGenBlockEntity>> STATIC_GLASS_GEN_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("static_glass_gen_block_entity",
            		() -> BlockEntityType.Builder.of(StaticGlassGenBlockEntity::new,
                            ModBlocks.STATIC_GLASS_GEN_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<GreebleGenBlockEntity>> GREEBLE_GEN_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("greeble_generator_block_entity",
            		() -> BlockEntityType.Builder.of(GreebleGenBlockEntity::new,
                            ModBlocks.GREEBLE_GEN_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<SparkChamberGenBlockEntity>> SPARK_CHAMBER_GEN_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("spark_chamber_generator_block_entity",
            		() -> BlockEntityType.Builder.of(SparkChamberGenBlockEntity::new,
                            ModBlocks.SPARK_CHAMBER_GEN_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<DeepKelpGenBlockEntity>> DEEPKELP_GEN_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("deepkelp_generator_block_entity",
            		() -> BlockEntityType.Builder.of(DeepKelpGenBlockEntity::new,
                            ModBlocks.DEEPKELP_GEN_BLOCK.get()).build(null));

    
    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
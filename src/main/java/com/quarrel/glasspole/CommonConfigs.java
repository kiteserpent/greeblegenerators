package com.quarrel.glasspole;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfigs {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> PEAK_GREEBLE_GEN_RATE;

    public static final ForgeConfigSpec.ConfigValue<Integer> SPARK_CHAMBER_SPARK;
    
    public static final ForgeConfigSpec.ConfigValue<Double> MAGMA_DIFFUSE_CHANCE;
    public static final ForgeConfigSpec.ConfigValue<Integer> DEEPKELP_GEN_RATE;
    public static final ForgeConfigSpec.ConfigValue<Integer> DEEPKELP_LOW_GEN_RATE;
    public static final ForgeConfigSpec.ConfigValue<Integer> KELP_BURN_TIME;
    public static final ForgeConfigSpec.ConfigValue<Integer> KELP_BLOCK_BURN_TIME;
    public static final ForgeConfigSpec.ConfigValue<Integer> KELPGEN_MIN_DEPTH;

    static {
        BUILDER.push("Configs for Greeble Generators");

        PEAK_GREEBLE_GEN_RATE = BUILDER.comment("FE/t produced by a fully fed Greeble Generator").defineInRange("Greeble peak rate", 50, 1, 5000);
        SPARK_CHAMBER_SPARK = BUILDER.comment("FE from a Spark Chamber event (1,400 is roughly 1 FE/t)").defineInRange("Spark FE", 5000, 1, 100000);
        MAGMA_DIFFUSE_CHANCE =  BUILDER.comment("Chance of sulfur-infused magma reverting on a random tick").defineInRange("Sulfur-Infused Magma revert chance", 0.92, 0.0, 1.0);
        DEEPKELP_GEN_RATE =  BUILDER.comment("FE/t produced by a Deep Kelp Generator over sulfur-infused magma").defineInRange("Deep Kelp FE/t w/infused magma", 100, 1, 50000);
        DEEPKELP_LOW_GEN_RATE =  BUILDER.comment("FE/t produced by a Deep Kelp Generator over normal magma").defineInRange("Deep Kelp FE/t w/normal magma", 10, 1, 50000);
        KELP_BURN_TIME =  BUILDER.comment("Production time for one piece of dried kelp").defineInRange("Dried Kelp power duration (ticks)", 10, 2, 10000);
        KELP_BLOCK_BURN_TIME =  BUILDER.comment("Production time for one block of dried kelp").defineInRange("Dried Kelp Block power duration (ticks)", 100, 2, 10000);
        KELPGEN_MIN_DEPTH =  BUILDER.comment("Minimum depth for a Keep Kelp Generator to operate").defineInRange("Depth", 20, 1, 500);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
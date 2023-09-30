package com.quarrel.glasspole;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfigs {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> PEAK_GREEBLE_GEN_RATE;
    public static final ForgeConfigSpec.ConfigValue<Integer> SPARK_CHAMBER_SPARK;

    static {
        BUILDER.push("Configs for Greeble Generators");

        PEAK_GREEBLE_GEN_RATE = BUILDER.comment("FE/t produced by a fully fed Greeble Generator").defineInRange("Greeble peak rate", 50, 1, 5000);
        SPARK_CHAMBER_SPARK = BUILDER.comment("FE from a Spark Chamber event (1,400 is roughly 1 FE/t)").defineInRange("Spark FE", 5000, 1, 100000);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
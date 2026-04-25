package com.magafin.allwithyou.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    public static final ModConfigSpec SERVER_SPEC;

    public static final ModConfigSpec.IntValue BACKPACK_CAPACITY;
    public static final ModConfigSpec.BooleanValue ENABLE_WEIGHT_PENALTY;
    public static final ModConfigSpec.IntValue WEIGHT_1;
    public static final ModConfigSpec.IntValue WEIGHT_2;
    public static final ModConfigSpec.IntValue WEIGHT_3;
    public static final ModConfigSpec.IntValue WEIGHT_4;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("Backpack Settings");

        BACKPACK_CAPACITY = builder
                .comment("Maximum capacity (weight) of the backpack. Vanilla bundle is 64. Default is 256 (4 stacks).")
                .defineInRange("backpackCapacity", 256, 64, 1024);

        ENABLE_WEIGHT_PENALTY = builder
                .comment("Enable slowness effect when carrying too many heavy backpacks.")
                .define("enableWeightPenalty", true);

        WEIGHT_1 = builder
                .comment("First level penalty.")
                .defineInRange("weight_1", 2, 1, 36);

        WEIGHT_2 = builder
                .comment("Second level penalty.")
                .defineInRange("weight_2", 4, 2, 36);

        WEIGHT_3 = builder
                .comment("Third level penalty.")
                .defineInRange("weight_3", 6, 3, 36);

        WEIGHT_4 = builder
                .comment("Fourth level penalty.")
                .defineInRange("weight_4", 8, 4, 36);

        builder.pop();

        SERVER_SPEC = builder.build();
    }
}
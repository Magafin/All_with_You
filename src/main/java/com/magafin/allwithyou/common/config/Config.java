package com.magafin.allwithyou.common.config;

import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    public static final ModConfigSpec SERVER_SPEC;

    public static ModConfigSpec.BooleanValue ENABLE_ZOMBIE_SPAWN_WITH_BACKPACK;
    public static ModConfigSpec.DoubleValue ZOMBIE_BACKPACK_SPAWN_CHANCE;
    public static ModConfigSpec.BooleanValue ENABLE_SKELETON_SPAWN_WITH_BACKPACK;
    public static ModConfigSpec.DoubleValue SKELETON_BACKPACK_SPAWN_CHANCE;
    public static final ModConfigSpec.IntValue BACKPACK_CAPACITY;
    public static final ModConfigSpec.BooleanValue ENABLE_WEIGHT_PENALTY;
    public static final ModConfigSpec.IntValue WEIGHT_1;
    public static final ModConfigSpec.IntValue WEIGHT_2;
    public static final ModConfigSpec.IntValue WEIGHT_3;
    public static final ModConfigSpec.IntValue WEIGHT_4;
    public static final ModConfigSpec.BooleanValue SUPPLEMENTARIES_COMPAT;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("Backpack Settings");

        ENABLE_ZOMBIE_SPAWN_WITH_BACKPACK = builder
                .comment("Should zombies have a chance to spawn with a backpack on their back?")
                .define("enableZombieSpawnWithBackpack", true);

        ZOMBIE_BACKPACK_SPAWN_CHANCE = builder
                .comment("The chance that a zombie will spawn with a backpack.")
                .defineInRange("zombieBackpackSpawnChance", 0.02, 0.0, 1.0);

        ENABLE_SKELETON_SPAWN_WITH_BACKPACK = builder
                .comment("Should skeletons have a chance to spawn with a backpack on their back?")
                .define("enableZombieSpawnWithBackpack", true);

        SKELETON_BACKPACK_SPAWN_CHANCE = builder
                .comment("The chance that a skeleton will spawn with a backpack.")
                .defineInRange("zombieBackpackSpawnChance", 0.02, 0.0, 1.0);

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

        SUPPLEMENTARIES_COMPAT =builder.
                comment("use Supplementaries overencumbered effect for tiredness")
                .define("supplementaries_compat", true);

        builder.pop();

        SERVER_SPEC = builder.build();
    }


    public static boolean supplementaries(){
        return Config.SUPPLEMENTARIES_COMPAT.getAsBoolean()&& ModList.get().isLoaded("supplementaries");
    }
}
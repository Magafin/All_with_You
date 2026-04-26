package com.magafin.allwithyou.common.register;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.magafin.allwithyou.all_with_you.All_with_you.MODID;

public class DataComponentsReg {
    public static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(MODID);

    public static final Supplier<DataComponentType<Integer>> SELECTED_ITEM_INDEX = COMPONENTS.registerComponentType(
            "selected_item_index",
            builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT)
    );
}

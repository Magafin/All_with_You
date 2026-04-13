package com.awy_magafin.common.register;

import com.awy_magafin.common.item.BackpackItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.BundleContents;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.awy_magafin.all_with_you.All_with_you.MODID;

public class ItemsReg {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredItem<Item> BACKPACK = ITEMS.registerItem("backpack",
            properties -> new BackpackItem(properties
                    .stacksTo(1)
                    .component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY)
                    .rarity(Rarity.UNCOMMON)
            ));
}

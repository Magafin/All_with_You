package com.magafin.allwithyou.common.mixin;

import com.magafin.allwithyou.common.config.Config;
import com.magafin.allwithyou.common.register.ItemsReg;
import net.mehvahdjukaar.supplementaries.common.items.SackItem;
import net.mehvahdjukaar.supplementaries.reg.ModTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SackItem.class)
public class SupplementariesCompatMixin {
    @Redirect(
            method = "getEncumber",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/tags/TagKey;)Z"
            )
    )
    private static boolean itemFilter(ItemStack instance, TagKey<Item> tag){
        return (instance.is(ModTags.OVERENCUMBERING)||
                (Config.supplementaries()&&instance.is(ItemsReg.BACKPACK)));
    }
}

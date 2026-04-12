package com.magafin.common.handler;

import com.magafin.common.register.ItemsReg;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = "all_with_you", bus = EventBusSubscriber.Bus.GAME)
public class BackpackTirednessHandler {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (!player.level().isClientSide && player.tickCount % 10 == 0) {
            int heavyBackpacks = 0;

            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (isHeavyBackpack(stack)) {
                    heavyBackpacks += stack.getCount();
                }
            }

            if (heavyBackpacks > 2) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 0, false, false, true));
            }
            if (heavyBackpacks > 4) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 1, false, false, true));
            }
            if (heavyBackpacks > 6) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 2, false, false, true));
            }
            if (heavyBackpacks > 8) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 4, false, false, true));
            }
        }
    }
    private static boolean isHeavyBackpack(ItemStack stack) {
        if (stack.is(ItemsReg.BACKPACK.get())) {
            net.minecraft.world.item.component.BundleContents contents = stack.get(net.minecraft.core.component.DataComponents.BUNDLE_CONTENTS);

            return contents != null && !contents.isEmpty();
        }
        return false;
    }
}

package com.magafin.allwithyou.common.handler;

import com.magafin.allwithyou.common.register.ItemsReg;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import com.magafin.allwithyou.common.config.Config;

@EventBusSubscriber(modid = "all_with_you", bus = EventBusSubscriber.Bus.GAME)
public class BackpackTirednessHandler {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        backpackTick(player);
    }

    private static void backpackTick(Player player){
        if(Config.supplementaries()||!Config.ENABLE_WEIGHT_PENALTY.get()) return;
        if(player.isCreative()) return;
        if (!player.level().isClientSide && player.tickCount % 10 == 0) {
            int heavyBackpacks = 0;

            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (isHeavyBackpack(stack)) {
                    heavyBackpacks += stack.getCount();
                }
            }
            int amplifier=-1;
            if (heavyBackpacks > Config.WEIGHT_4.get()) {
                amplifier=3;
            } else if (heavyBackpacks > Config.WEIGHT_3.get()) {
                amplifier=2;
            } else if (heavyBackpacks > Config.WEIGHT_2.get()) {
                amplifier=1;
            } else if (heavyBackpacks > Config.WEIGHT_1.get()) {
                amplifier=0;
            }
            if(amplifier>-1){
                player.addEffect(
                        new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
                                20,
                                amplifier,
                                false,
                                false,
                                true
                        ));
            }
        }
    }

    private static boolean isHeavyBackpack(ItemStack stack) {
        if (stack.is(ItemsReg.BACKPACK.get())) {
            BundleContents contents = stack.get(net.minecraft.core.component.DataComponents.BUNDLE_CONTENTS);
            return contents != null && !contents.isEmpty();
        }
        return false;
    }
}

package com.magafin.allwithyou.client;

import com.magafin.allwithyou.common.item.BackpackTooltip;
import com.magafin.allwithyou.common.network.BackpackStorePayload;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import net.neoforged.neoforge.network.PacketDistributor;

public class KeybindSetup {

    public static final KeyMapping STORE_IN_BACKPACK = new KeyMapping(
            "key.all_with_you.store_in_backpack",
            KeyConflictContext.IN_GAME,
            KeyModifier.ALT,
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_Q,
            "key.categories.all_with_you"
    );

    @EventBusSubscriber(modid = "all_with_you", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModBusEvents {
        @SubscribeEvent
        public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
            event.register(STORE_IN_BACKPACK);
        }
        @SubscribeEvent
        public static void registerTooltipComponent(net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent event) {
            event.register(BackpackTooltip.class, ClientBackpackTooltip::new);
        }
    }

    @EventBusSubscriber(modid = "all_with_you", bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static class ClientForgeBusEvents {
        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            while (STORE_IN_BACKPACK.consumeClick()) {
                PacketDistributor.sendToServer(new BackpackStorePayload());
            }
        }
    }
}

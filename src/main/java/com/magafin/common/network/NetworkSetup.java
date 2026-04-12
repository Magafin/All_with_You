package com.magafin.common.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = "all_with_you", bus = EventBusSubscriber.Bus.MOD)
public class NetworkSetup {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1.0");
        registrar.playToServer(
                BackpackStorePayload.TYPE,
                BackpackStorePayload.CODEC,
                BackpackStorePayload::handle
        );
    }
}
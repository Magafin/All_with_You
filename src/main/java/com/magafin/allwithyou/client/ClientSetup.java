package com.magafin.allwithyou.client;

import com.magafin.allwithyou.client.model.BackpackOnPlayer;
import com.magafin.allwithyou.client.renderer.BackpackLayer;
import com.magafin.allwithyou.common.config.Config;
import com.magafin.allwithyou.common.item.BackpackItem;
import com.magafin.allwithyou.common.register.ItemsReg;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid = "all_with_you", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(
                    ItemsReg.BACKPACK.get(),
                    ResourceLocation.fromNamespaceAndPath("all_with_you", "fullness"),
                    (stack, level, entity, seed) -> {
                        int currentWeight = BackpackItem.getContentsWeight(stack);
                        if (currentWeight <= 0) {
                            return 0.0F;
                        }
                        float maxCapacity = (float) Config.BACKPACK_CAPACITY.get();
                        return Math.max(0.01F, (float) currentWeight / maxCapacity);
                    }
            );
        });
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
            if (tintIndex == 0) {
                return net.minecraft.world.item.component.DyedItemColor.getOrDefault(stack, 0xFFFFFFFF);
            }
            return -1;
        }, ItemsReg.BACKPACK.get());
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BackpackOnPlayer.LAYER_LOCATION, BackpackOnPlayer::createBodyLayer);
    }

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        for (net.minecraft.client.resources.PlayerSkin.Model skinType : event.getSkins()) {
            net.minecraft.client.renderer.entity.LivingEntityRenderer<net.minecraft.client.player.AbstractClientPlayer, net.minecraft.client.model.PlayerModel<net.minecraft.client.player.AbstractClientPlayer>> renderer = event.getSkin(skinType);

            if (renderer != null) {
                renderer.addLayer(new BackpackLayer<>(renderer));
            }
        }

        net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.stream()
                .map(event::getRenderer)
                .filter(renderer -> renderer instanceof net.minecraft.client.renderer.entity.LivingEntityRenderer)
                .map(renderer -> (net.minecraft.client.renderer.entity.LivingEntityRenderer<?, ?>) renderer)
                .filter(livingRenderer -> livingRenderer.getModel() instanceof net.minecraft.client.model.HumanoidModel)
                .forEach(livingRenderer -> {
                    @SuppressWarnings({"rawtypes", "unchecked"})
                    BackpackLayer layer = new BackpackLayer(livingRenderer);
                    livingRenderer.addLayer(layer);
                });
    }
}
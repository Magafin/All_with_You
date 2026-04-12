package com.magafin.client;

import com.magafin.client.model.BackpackOnPlayer;
import com.magafin.client.renderer.BackpackLayer;
import com.magafin.common.item.BackpackItem;
import com.magafin.common.register.ItemsReg;
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
                            return 0.0F; // Если пустой — возвращаем строго 0, полоска исчезнет
                        }
                        // Если есть хоть один предмет, считаем процент заполнения
                        return Math.max(0.01F, (float) currentWeight / 256.0F);
                    }
            );
        });
    }
    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BackpackOnPlayer.LAYER_LOCATION, BackpackOnPlayer::createBodyLayer);
    }
    @SubscribeEvent
    public static void onAddLayers(net.neoforged.neoforge.client.event.EntityRenderersEvent.AddLayers event) {
        for (net.minecraft.client.resources.PlayerSkin.Model skinType : event.getSkins()) {
            net.minecraft.client.renderer.entity.LivingEntityRenderer<net.minecraft.client.player.AbstractClientPlayer, net.minecraft.client.model.PlayerModel<net.minecraft.client.player.AbstractClientPlayer>> renderer = event.getSkin(skinType);

            if (renderer != null) {
                renderer.addLayer(new BackpackLayer<>(renderer));
            }
        }

        net.minecraft.client.renderer.entity.EntityRenderer<?> armorStandRenderer = event.getRenderer(net.minecraft.world.entity.EntityType.ARMOR_STAND);

        if (armorStandRenderer instanceof net.minecraft.client.renderer.entity.LivingEntityRenderer livingRenderer) {
            @SuppressWarnings({"rawtypes", "unchecked"})
            BackpackLayer armorStandLayer = new BackpackLayer(livingRenderer);

            livingRenderer.addLayer(armorStandLayer);
        }

    }
}

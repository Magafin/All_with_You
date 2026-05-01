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
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

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
    @SubscribeEvent
    public static void addBuiltInPacks(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            String modId = "all_with_you";

            Map<String, String> packs = Map.of(
                    "nomansland_compat", "All with You: No Man's Land",
                    "darkmode", "All with You: Dark Mode",
                    "default", "All with You: Default Mode"
            );

            packs.forEach((folderName, displayName) -> {
                Path resourcePath = ModList.get().getModFileById(modId).getFile().findResource("resourcepacks/" + folderName);

                Pack pack = Pack.readMetaAndCreate(
                        new PackLocationInfo(
                                modId + ":" + folderName,
                                Component.literal(displayName),
                                PackSource.BUILT_IN,
                                Optional.empty()
                        ),
                        new PathPackResources.PathResourcesSupplier(resourcePath),
                        PackType.CLIENT_RESOURCES,
                        new PackSelectionConfig(false, Pack.Position.BOTTOM, false)
                );

                if (pack != null) {
                    event.addRepositorySource(infoConsumer -> infoConsumer.accept(pack));
                }
            });
        }
    }
}

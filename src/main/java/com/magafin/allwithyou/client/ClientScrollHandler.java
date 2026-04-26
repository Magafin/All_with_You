package com.magafin.allwithyou.client;

import com.magafin.allwithyou.common.item.BackpackItem;
import com.magafin.allwithyou.common.network.BackpackScrollPayload;
import com.magafin.allwithyou.common.register.DataComponentsReg;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = "all_with_you", bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientScrollHandler {

    @SubscribeEvent
    public static void onMouseScroll(ScreenEvent.MouseScrolled.Pre event) {
        if (event.getScreen() instanceof AbstractContainerScreen<?> screen) {
            Slot slot = screen.getSlotUnderMouse();
            if (slot != null && slot.hasItem() && slot.getItem().getItem() instanceof BackpackItem) {
                ItemStack backpack = slot.getItem();
                BundleContents contents = backpack.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);

                if (!contents.isEmpty()) {
                    int currentIndex = backpack.getOrDefault(DataComponentsReg.SELECTED_ITEM_INDEX.get(), 0);
                    int delta = (int) Math.signum(event.getScrollDeltaY());

                    int newIndex = currentIndex - delta;

                    // Зацикливаем выбор
                    if (newIndex < 0) {
                        newIndex = contents.size() - 1;
                    } else if (newIndex >= contents.size()) {
                        newIndex = 0;
                    }

                    if (currentIndex != newIndex) {
                        PacketDistributor.sendToServer(new BackpackScrollPayload(slot.index, newIndex));
                        // Мгновенно обновляем на клиенте для плавности визуала
                        backpack.set(DataComponentsReg.SELECTED_ITEM_INDEX.get(), newIndex);
                    }

                    event.setCanceled(true); // Блокируем стандартную прокрутку (например, в JEI)
                }
            }
        }
    }
}

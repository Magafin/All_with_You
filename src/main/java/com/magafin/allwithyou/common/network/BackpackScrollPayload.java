package com.magafin.allwithyou.common.network;

import com.magafin.allwithyou.common.item.BackpackItem;
import com.magafin.allwithyou.common.register.DataComponentsReg;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record BackpackScrollPayload(int slotId, int newIndex) implements CustomPacketPayload {
    public static final Type<BackpackScrollPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("all_with_you", "backpack_scroll"));

    public static final StreamCodec<FriendlyByteBuf, BackpackScrollPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, BackpackScrollPayload::slotId,
            ByteBufCodecs.INT, BackpackScrollPayload::newIndex,
            BackpackScrollPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(BackpackScrollPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player == null) return;

            AbstractContainerMenu menu = player.containerMenu;
            if (payload.slotId() >= 0 && payload.slotId() < menu.slots.size()) {
                Slot slot = menu.slots.get(payload.slotId());
                ItemStack stack = slot.getItem();

                if (stack.getItem() instanceof BackpackItem) {
                    stack.set(DataComponentsReg.SELECTED_ITEM_INDEX.get(), payload.newIndex());
                }
            }
        });
    }
}

package com.magafin.allwithyou.common.network;

import com.magafin.allwithyou.common.item.BackpackItem;
import com.magafin.allwithyou.common.register.ItemsReg;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record BackpackStorePayload() implements CustomPacketPayload {
    public static final Type<BackpackStorePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("all_with_you", "backpack_store"));
    public static final StreamCodec<FriendlyByteBuf, BackpackStorePayload> CODEC = StreamCodec.unit(new BackpackStorePayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(BackpackStorePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player == null) return;

            ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
            if (chestStack.is(ItemsReg.BACKPACK.get())) {

                ItemStack mainHand = player.getMainHandItem();

                if (BackpackItem.isForbiddenContainer(mainHand)) {
                    player.displayClientMessage(
                            net.minecraft.network.chat.Component.translatable("message.all_with_you.backpack.forbidden"),
                            true
                    );
                    return;
                }

                if (mainHand.isEmpty()) return;

                int initialCount = mainHand.getCount();

                BackpackItem.customInsert(chestStack, mainHand, player);

                if (mainHand.getCount() < initialCount) {
                    player.level().playSound(
                            null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.BUNDLE_INSERT,
                            net.minecraft.sounds.SoundSource.PLAYERS,
                            0.5F, 0.8F + player.level().getRandom().nextFloat() * 0.4F
                    );

                    if (!mainHand.isEmpty()) {
                        player.displayClientMessage(
                                net.minecraft.network.chat.Component.translatable("message.all_with_you.backpack.full"),
                                true
                        );
                    }
                } else {
                    player.displayClientMessage(
                            net.minecraft.network.chat.Component.translatable("message.all_with_you.backpack.no_space"),
                            true
                    );
                }
            }
        });
    }
}

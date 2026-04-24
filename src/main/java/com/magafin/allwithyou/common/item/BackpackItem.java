package com.magafin.allwithyou.common.item;

import com.magafin.allwithyou.client.model.BackpackOnPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.ChatFormatting;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BackpackItem extends BundleItem implements Equipable {
    private static final int MAX_CAPACITY = 256;

    public BackpackItem(Properties properties) {
        super(properties); // Возвращаем обычный конструктор
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, net.minecraft.world.inventory.Slot slot, net.minecraft.world.inventory.ClickAction action, net.minecraft.world.entity.player.Player player, net.minecraft.world.entity.SlotAccess slotAccess) {
        if (action == net.minecraft.world.inventory.ClickAction.SECONDARY) {
            if (other.isEmpty()) {
                return super.overrideOtherStackedOnMe(stack, other, slot, action, player, slotAccess);
            } else {
                if (isForbiddenContainer(other)) return true;

                int initialCount = other.getCount();
                customInsert(stack, other);

                if (other.getCount() < initialCount) {
                    player.playSound(net.minecraft.sounds.SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + player.level().getRandom().nextFloat() * 0.4F);
                }
                return true;
            }
        }
        return super.overrideOtherStackedOnMe(stack, other, slot, action, player, slotAccess);
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stack, net.minecraft.world.inventory.Slot slot, net.minecraft.world.inventory.ClickAction action, net.minecraft.world.entity.player.Player player) {
        if (action == net.minecraft.world.inventory.ClickAction.SECONDARY) {
            ItemStack itemInSlot = slot.getItem();
            if (itemInSlot.isEmpty()) {
                return super.overrideStackedOnOther(stack, slot, action, player);
            } else {
                if (isForbiddenContainer(itemInSlot)) return true;

                int initialCount = itemInSlot.getCount();
                customInsert(stack, itemInSlot);

                if (itemInSlot.getCount() < initialCount) {
                    player.playSound(net.minecraft.sounds.SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + player.level().getRandom().nextFloat() * 0.4F);
                    slot.set(itemInSlot);
                }
                return true;
            }
        }
        
        return super.overrideStackedOnOther(stack, slot, action, player);
    }
    @Override
    public java.util.Optional<net.minecraft.world.inventory.tooltip.TooltipComponent> getTooltipImage(ItemStack stack) {
        return Optional.of(new BackpackTooltip(stack.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY)));
    }
    public static boolean isForbiddenContainer(ItemStack stack) {
        if (stack.isEmpty()) return false;

        if (stack.getItem() instanceof net.minecraft.world.item.BundleItem) {
            return true;
        }

        if (stack.has(net.minecraft.core.component.DataComponents.CONTAINER)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }

    private boolean canFitInside(ItemStack stack) {
        return !(stack.getItem() instanceof BundleItem) && !(stack.getItem() instanceof BackpackItem);
    }
    private int tryInsert(ItemStack backpack, ItemStack stack) {
        if (stack.isEmpty() || !canFitInside(stack)) return 0;

        BundleContents contents = backpack.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
        List<ItemStack> currentItems = new ArrayList<>();
        contents.items().forEach(item -> currentItems.add(item.copy()));

        int currentWeight = getContentsWeight(currentItems);
        int itemWeight = getWeight(stack);
        int remainingWeight = MAX_CAPACITY - currentWeight;

        if (remainingWeight < itemWeight) return 0;

        int maxAmountToAdd = Math.min(stack.getCount(), remainingWeight / itemWeight);
        if (maxAmountToAdd == 0) return 0;

        ItemStack toInsert = stack.copyWithCount(maxAmountToAdd);
        List<ItemStack> newItems = mergeIntoList(currentItems, toInsert);

        backpack.set(DataComponents.BUNDLE_CONTENTS, new BundleContents(newItems));
        stack.shrink(maxAmountToAdd);

        return maxAmountToAdd;
    }

    private Optional<ItemStack> removeOne(ItemStack backpack) {
        BundleContents contents = backpack.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
        List<ItemStack> currentItems = new ArrayList<>();
        contents.items().forEach(item -> currentItems.add(item.copy()));

        if (currentItems.isEmpty()) return Optional.empty();

        ItemStack removed = currentItems.remove(0);
        backpack.set(DataComponents.BUNDLE_CONTENTS, new BundleContents(currentItems));
        return Optional.of(removed);
    }

    private static int getWeight(ItemStack stack) {
        return 64 / stack.getMaxStackSize();
    }

    public static int getContentsWeight(ItemStack backpack) {
        net.minecraft.world.item.component.BundleContents contents = backpack.getOrDefault(net.minecraft.core.component.DataComponents.BUNDLE_CONTENTS, net.minecraft.world.item.component.BundleContents.EMPTY);
        int weight = 0;
        for (ItemStack item : contents.items()) {
            int itemWeight = 64 / Math.max(1, item.getMaxStackSize());
            weight += itemWeight * item.getCount();
        }
        return weight;
    }
    public static ItemStack customInsert(ItemStack backpack, ItemStack toInsert) {
        if (toInsert.isEmpty() || isForbiddenContainer(toInsert)) return toInsert;

        net.minecraft.world.item.component.BundleContents contents = backpack.getOrDefault(net.minecraft.core.component.DataComponents.BUNDLE_CONTENTS, net.minecraft.world.item.component.BundleContents.EMPTY);
        java.util.List<ItemStack> items = new java.util.ArrayList<>();
        contents.items().forEach(items::add);

        int currentWeight = getContentsWeight(backpack);
        int spaceLeft = MAX_CAPACITY - currentWeight;

        if (spaceLeft <= 0) return toInsert;

        int itemWeight = 64 / Math.max(1, toInsert.getMaxStackSize());
        int maxItemsToInsert = spaceLeft / itemWeight;

        if (maxItemsToInsert <= 0) return toInsert;

        int amountToInsert = Math.min(toInsert.getCount(), maxItemsToInsert);
        ItemStack insertedStack = toInsert.copyWithCount(amountToInsert);

        for (int i = 0; i < items.size(); i++) {
            ItemStack existing = items.get(i);
            if (ItemStack.isSameItemSameComponents(existing, insertedStack)) {
                int spaceInStack = existing.getMaxStackSize() - existing.getCount();
                int toAdd = Math.min(amountToInsert, spaceInStack);
                if (toAdd > 0) {
                    existing.grow(toAdd);
                    amountToInsert -= toAdd;
                    insertedStack.shrink(toAdd);
                }
                if (amountToInsert <= 0) break;
            }
        }

        if (amountToInsert > 0) {
            items.add(0, insertedStack);
        }

        backpack.set(net.minecraft.core.component.DataComponents.BUNDLE_CONTENTS, new net.minecraft.world.item.component.BundleContents(items));
        toInsert.shrink(Math.min(toInsert.getCount(), maxItemsToInsert));

        return toInsert;
    }

    public static int getContentsWeight(List<ItemStack> items) {
        int totalWeight = 0;
        for (ItemStack item : items) {
            totalWeight += getWeight(item) * item.getCount();
        }
        return totalWeight;
    }

    private static List<ItemStack> mergeIntoList(List<ItemStack> list, ItemStack stackToAdd) {
        List<ItemStack> newList = new ArrayList<>(list);

        for (int i = 0; i < newList.size(); i++) {
            ItemStack existing = newList.get(i);
            if (ItemStack.isSameItemSameComponents(existing, stackToAdd)) {
                int space = existing.getMaxStackSize() - existing.getCount();
                int toAdd = Math.min(space, stackToAdd.getCount());
                if (toAdd > 0) {
                    existing.grow(toAdd);
                    stackToAdd.shrink(toAdd);
                }
            }
            if (stackToAdd.isEmpty()) break;
        }

        if (!stackToAdd.isEmpty()) {
            newList.add(0, stackToAdd.copy());
            stackToAdd.setCount(0);
        }
        return newList;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        // Полоска видна только если вес больше 0
        return getContentsWeight(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.min(1 + 12 * getContentsWeight(stack) / MAX_CAPACITY, 13);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0xffa923;
    }
    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.CHEST;
    }

    private void playRemoveOneSound(Player player) {
        player.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.5F, 0.8F + player.level().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Player player) {
        player.playSound(SoundEvents.BUNDLE_INSERT, 0.5F, 0.8F + player.level().getRandom().nextFloat() * 0.4F);
    }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        int currentWeight = getContentsWeight(stack);
        tooltipComponents.add(Component.translatable("item.minecraft.bundle.fullness", currentWeight, MAX_CAPACITY).withStyle(ChatFormatting.GRAY));
    }
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BackpackOnPlayer model = null;

            @Override
            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> _default) {
                if (armorSlot == EquipmentSlot.CHEST) {
                    if (model == null) {
                        model = new BackpackOnPlayer(Minecraft.getInstance().getEntityModels().bakeLayer(BackpackOnPlayer.LAYER_LOCATION));
                    }

                    ((HumanoidModel)_default).copyPropertiesTo(model);

                    model.head.visible = false;
                    model.hat.visible = false;
                    model.leftArm.visible = false;
                    model.rightArm.visible = false;
                    model.leftLeg.visible = false;
                    model.rightLeg.visible = false;

                    model.body.visible = true;

                    return model;
                }
                return _default;
            }
        });
    }
}

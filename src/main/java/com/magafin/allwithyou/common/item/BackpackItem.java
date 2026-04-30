package com.magafin.allwithyou.common.item;

import com.magafin.allwithyou.client.model.BackpackOnPlayer;
import com.magafin.allwithyou.common.config.Config;
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


    public BackpackItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, net.minecraft.world.inventory.Slot slot, net.minecraft.world.inventory.ClickAction action, net.minecraft.world.entity.player.Player player, net.minecraft.world.entity.SlotAccess slotAccess) {
        if (action == net.minecraft.world.inventory.ClickAction.SECONDARY && slot.allowModification(player)) {
            if (other.isEmpty()) {
                Optional<ItemStack> removed = removeSelectedItem(stack);
                removed.ifPresent(itemStack -> {
                    player.playSound(net.minecraft.sounds.SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + player.level().getRandom().nextFloat() * 0.4F);
                    slotAccess.set(itemStack);
                });
                if (isForbiddenContainer(other)) return true;
                return true;
            } else {
                int initialCount = other.getCount();
                customInsert(stack, other);
                if (other.getCount() < initialCount) {
                    player.playSound(net.minecraft.sounds.SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + player.level().getRandom().nextFloat() * 0.4F);
                }
                return true;
            }
        }
        return false;
    }

    private Optional<ItemStack> removeSelectedItem(ItemStack backpack) {
        BundleContents contents = backpack.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
        if (contents.isEmpty()) return Optional.empty();

        List<ItemStack> currentItems = new ArrayList<>();
        contents.items().forEach(item -> currentItems.add(item.copy()));

        int selectedIndex = backpack.getOrDefault(com.magafin.allwithyou.common.register.DataComponentsReg.SELECTED_ITEM_INDEX.get(), 0);

        if (selectedIndex < 0 || selectedIndex >= currentItems.size()) {
            selectedIndex = 0;
        }

        ItemStack removed = currentItems.remove(selectedIndex);
        backpack.set(DataComponents.BUNDLE_CONTENTS, new BundleContents(currentItems));

        if (selectedIndex >= currentItems.size() && !currentItems.isEmpty()) {
            backpack.set(com.magafin.allwithyou.common.register.DataComponentsReg.SELECTED_ITEM_INDEX.get(), currentItems.size() - 1);
        } else if (currentItems.isEmpty()) {
            backpack.set(com.magafin.allwithyou.common.register.DataComponentsReg.SELECTED_ITEM_INDEX.get(), 0);
        }

        return Optional.of(removed);
    }

    public static boolean isForbiddenContainer(ItemStack stack) {
        if (stack.isEmpty()) return false;

        // 1. Самая важная ванильная проверка.
        // Она возвращает 'false' для Шалкеров, поэтому мы инвертируем (!).
        if (!stack.getItem().canFitInsideContainerItems()) {
            return true;
        }

        // 2. Запрещаем класть мешочки в мешочки (и рюкзаки в рюкзаки).
        // Так как твой BackpackItem наследуется от BundleItem, это условие
        // автоматически запретит класть и ванильные мешочки, и твои рюкзаки друг в друга.
        if (stack.getItem() instanceof net.minecraft.world.item.BundleItem) {
            return true;
        }

        return false;
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stack, net.minecraft.world.inventory.Slot slot, net.minecraft.world.inventory.ClickAction action, net.minecraft.world.entity.player.Player player) {
        if (action == net.minecraft.world.inventory.ClickAction.SECONDARY && slot.allowModification(player)) {
            ItemStack itemInSlot = slot.getItem();
            if (isForbiddenContainer(itemInSlot)) return true;
            if (itemInSlot.isEmpty()) {
                Optional<ItemStack> removed = removeSelectedItem(stack);
                removed.ifPresent(itemStack -> {
                    player.playSound(net.minecraft.sounds.SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + player.level().getRandom().nextFloat() * 0.4F);
                    slot.set(itemStack);
                });
                return true;
            } else {
                int initialCount = itemInSlot.getCount();
                customInsert(stack, itemInSlot);
                if (itemInSlot.getCount() < initialCount) {
                    player.playSound(net.minecraft.sounds.SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + player.level().getRandom().nextFloat() * 0.4F);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public java.util.Optional<net.minecraft.world.inventory.tooltip.TooltipComponent> getTooltipImage(ItemStack stack) {
        int selectedIndex = stack.getOrDefault(com.magafin.allwithyou.common.register.DataComponentsReg.SELECTED_ITEM_INDEX.get(), 0);
        return Optional.of(new BackpackTooltip(stack.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY), selectedIndex));
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
        int remainingWeight = Config.BACKPACK_CAPACITY.get() - currentWeight;

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

        if (toInsert.isEmpty() || isForbiddenContainer(toInsert)) {
            return toInsert;
        }

        net.minecraft.world.item.component.BundleContents contents = backpack.getOrDefault(net.minecraft.core.component.DataComponents.BUNDLE_CONTENTS, net.minecraft.world.item.component.BundleContents.EMPTY);
        java.util.List<ItemStack> items = new java.util.ArrayList<>();
        contents.items().forEach(items::add);

        int currentWeight = getContentsWeight(backpack);
        int spaceLeft = Config.BACKPACK_CAPACITY.get() - currentWeight;

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
        return Math.min(13, (int) Math.ceil(13.0D * (double) getContentsWeight(stack) / (double) Config.BACKPACK_CAPACITY.get()));
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
        int maxCapacity = Config.BACKPACK_CAPACITY.get(); // БЕРЕМ ИЗ КОНФИГА
        tooltipComponents.add(Component.translatable("item.minecraft.bundle.fullness", currentWeight, maxCapacity).withStyle(ChatFormatting.GRAY));
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
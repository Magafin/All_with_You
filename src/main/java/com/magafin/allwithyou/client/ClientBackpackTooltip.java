package com.magafin.allwithyou.client;

import com.magafin.allwithyou.common.item.BackpackItem;
import com.magafin.allwithyou.common.item.BackpackTooltip;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import com.magafin.allwithyou.common.config.Config;

import java.util.ArrayList;
import java.util.List;

public class ClientBackpackTooltip implements ClientTooltipComponent {
    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.fromNamespaceAndPath("all_with_you", "tooltip/backpack_background");
    private static final ResourceLocation SLOT_SPRITE = ResourceLocation.fromNamespaceAndPath("all_with_you", "tooltip/backpack_slot");
    private static final ResourceLocation BLOCKED_SLOT_SPRITE = ResourceLocation.fromNamespaceAndPath("all_with_you", "tooltip/backpack_blocked_slot");
    private static final ResourceLocation SELECTED_SLOT_SPRITE = ResourceLocation.fromNamespaceAndPath("all_with_you", "tooltip/backpack_selected_slot");

    private final BackpackTooltip tooltip;
    private final List<ItemStack> items = new ArrayList<>();
    private final int totalWeight;
    private final int selectedIndex;

    public ClientBackpackTooltip(BackpackTooltip tooltip) {
        this.tooltip = tooltip;
        tooltip.contents().items().forEach(this.items::add);

        this.totalWeight = BackpackItem.getContentsWeight(this.items);
        this.selectedIndex = tooltip.selectedIndex();
    }

    @Override
    public int getHeight() {
        return this.gridHeight() + 4;
    }

    @Override
    public int getWidth(Font font) {
        return this.gridWidth();
    }

    private int gridWidth() {
        return this.columns() * 18 + 2;
    }

    private int gridHeight() {
        return this.rows() * 20 + 2;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        int cols = this.columns();
        int rows = this.rows();
        graphics.blitSprite(BACKGROUND_SPRITE, x, y, this.gridWidth(), this.gridHeight());

        int itemIndex = 0;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int slotX = x + c * 18 + 1;
                int slotY = y + r * 20 + 1;
                this.renderSlot(slotX, slotY, itemIndex++, this.items, graphics, font);
            }
        }
    }

    private void renderSlot(int x, int y, int index, List<ItemStack> items, GuiGraphics graphics, Font font) {
        if (index >= items.size()) {
            if (this.totalWeight >= Config.BACKPACK_CAPACITY.get()) {
                graphics.blitSprite(BLOCKED_SLOT_SPRITE, x, y, 18, 20);
            } else {
                graphics.blitSprite(SLOT_SPRITE, x, y, 18, 20);
            }
        } else {
            ItemStack stack = items.get(index);

            graphics.blitSprite(SLOT_SPRITE, x, y, 18, 20);

            graphics.renderItem(stack, x + 1, y + 1);
            graphics.renderItemDecorations(font, stack, x + 1, y + 1);

            if (index == this.selectedIndex) {
                graphics.blitSprite(SELECTED_SLOT_SPRITE, x, y, 18, 20);
            }
        }
    }

    private int columns() {
        return Math.max(2, (int)Math.ceil(Math.sqrt((double)this.items.size() + 1.0)));
    }

    private int rows() {
        return (int)Math.ceil(((double)this.items.size() + 1.0) / (double)this.columns());
    }
}
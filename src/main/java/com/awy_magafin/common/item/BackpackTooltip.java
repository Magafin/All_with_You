package com.awy_magafin.common.item;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.component.BundleContents;

public record BackpackTooltip(BundleContents contents) implements TooltipComponent {
}

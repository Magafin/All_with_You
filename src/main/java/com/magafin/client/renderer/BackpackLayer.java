package com.magafin.client.renderer;

import com.magafin.client.model.BackpackOnPlayer;
import com.magafin.common.item.BackpackItem;
import com.magafin.common.register.ItemsReg;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class BackpackLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {
    private final BackpackOnPlayer backpackModel;

    public BackpackLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
        this.backpackModel = new BackpackOnPlayer(Minecraft.getInstance().getEntityModels().bakeLayer(BackpackOnPlayer.LAYER_LOCATION));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack chestStack = entity.getItemBySlot(EquipmentSlot.CHEST);

        if (chestStack.is(ItemsReg.BACKPACK.get())) {
            poseStack.pushPose();

            M playerModel = this.getParentModel();
            playerModel.copyPropertiesTo((HumanoidModel<T>) this.backpackModel);
            this.backpackModel.body.copyFrom(playerModel.body);

            int color = 0xFFFFFFFF;

            ResourceLocation baseTexture = ResourceLocation.fromNamespaceAndPath("all_with_you", "textures/entity/backpack_model_base.png");
            VertexConsumer baseConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(baseTexture));

            this.backpackModel.renderToBuffer(poseStack, baseConsumer, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), color);

            ResourceLocation overlayTexture = ResourceLocation.fromNamespaceAndPath("all_with_you", "textures/entity/backpack_model_overlay.png");
            VertexConsumer overlayConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(overlayTexture));

            this.backpackModel.renderToBuffer(poseStack, overlayConsumer, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 0xFFFFFFFF);

            poseStack.popPose();
        }
    }
}
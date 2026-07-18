package com.magafin.allwithyou.client.renderer;

import com.magafin.allwithyou.client.model.BackpackOnPlayer;
import com.magafin.allwithyou.common.register.ItemsReg;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.hardaway.mannequins.common.entity.ClientDummy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.Rotations;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;

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
            M playerModel = this.getParentModel();
            poseStack.pushPose();

            playerModel.body.translateAndRotate(poseStack);

            if (playerModel.young) {
                poseStack.scale(0.5F, 0.5F, 0.5F);
                poseStack.translate(0.0F, 24.0F / 16.0F, 0.0F);
            }
            if(entity instanceof ClientDummy dummy){
                Rotations bodyRot=dummy.getDummy().getPose().body();
                this.backpackModel.body.setRotation((float) Math.toRadians(bodyRot.getX()), (float) Math.toRadians(bodyRot.getY()), (float) Math.toRadians(bodyRot.getZ()));
                poseStack.translate(0.0F, 12.0F / 16.0F, 0.0F);
            }

            ModelPart backpackMesh = this.backpackModel.body;

            DyedItemColor dyedColor = chestStack.get(DataComponents.DYED_COLOR);
            int colorRgb = dyedColor != null ? dyedColor.rgb() : 0xFFFFFF;
            int overlayCoords = LivingEntityRenderer.getOverlayCoords(entity, 0.0F);

            ResourceLocation baseTexture = ResourceLocation.fromNamespaceAndPath("all_with_you", "textures/entity/backpack_model_base.png");
            VertexConsumer baseConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(baseTexture));
            backpackMesh.render(poseStack, baseConsumer, packedLight, overlayCoords, colorRgb | 0xFF000000);

            ResourceLocation overlayTexture = ResourceLocation.fromNamespaceAndPath("all_with_you", "textures/entity/backpack_model_overlay.png");
            VertexConsumer overlayConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(overlayTexture));
            backpackMesh.render(poseStack, overlayConsumer, packedLight, overlayCoords, 0xFFFFFFFF);

            poseStack.popPose();
        }
    }
}
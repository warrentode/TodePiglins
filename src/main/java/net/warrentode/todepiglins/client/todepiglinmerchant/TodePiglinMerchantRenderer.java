package net.warrentode.todepiglins.client.todepiglinmerchant;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.block.state.BlockState;
import net.warrentode.todepiglins.entity.custom.todepiglinmerchant.TodePiglinMerchant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.ExtendedGeoEntityRenderer;

import static net.warrentode.todepiglins.TodePiglins.MODID;

public class TodePiglinMerchantRenderer extends ExtendedGeoEntityRenderer<TodePiglinMerchant> {

    protected ItemStack mainHandItem, offHandItem, helmetItem, chestplateItem, leggingsItem, bootsItem;
    public TodePiglinMerchantRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TodePiglinMerchantModel());
        this.shadowRadius = 0.6f;
    }
    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull TodePiglinMerchant instance) {
        return new ResourceLocation(MODID, "textures/entity/todepiglin/todepiglinmerchant.png");
    }
    @Override
    public RenderType getRenderType(TodePiglinMerchant animatable, float partialTick, PoseStack poseStack,
                                    @Nullable MultiBufferSource bufferSource,
                                    @Nullable VertexConsumer buffer, int packedLight, ResourceLocation texture) {

        return super.getRenderType(animatable, partialTick, poseStack, bufferSource, buffer, packedLight, texture);
    }
    @Override
    public void renderEarly(TodePiglinMerchant animatable, PoseStack poseStack, float partialTick, MultiBufferSource bufferSource,
                            VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float partialTicks) {
        super.renderEarly(animatable, poseStack, partialTick, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, partialTicks);

        this.mainHandItem = animatable.getItemBySlot(EquipmentSlot.MAINHAND);
        this.offHandItem = animatable.getItemBySlot(EquipmentSlot.OFFHAND);
        this.helmetItem = animatable.getItemBySlot(EquipmentSlot.HEAD);
        this.chestplateItem = animatable.getItemBySlot(EquipmentSlot.CHEST);
        this.leggingsItem = animatable.getItemBySlot(EquipmentSlot.LEGS);
        this.bootsItem = animatable.getItemBySlot(EquipmentSlot.FEET);
    }

    @Override
    protected ResourceLocation getTextureForBone(String boneName, TodePiglinMerchant animatable) {
        return null;
    }
    @Override
    protected boolean isArmorBone(@NotNull GeoBone bone) {
        return bone.getName().startsWith("armor");
    }
    @Override
    protected ItemStack getArmorForBone(@NotNull String boneName, TodePiglinMerchant currentEntity) {
        return switch (boneName) {
            case TPMBoneIndents.LEFT_FOOT_ARMOR_BONE_INDENT,
                    TPMBoneIndents.RIGHT_FOOT_ARMOR_BONE_INDENT -> this.bootsItem;
            case TPMBoneIndents.LEFT_LEG_ARMOR_BONE_INDENT,
                    TPMBoneIndents.RIGHT_LEG_ARMOR_BONE_INDENT -> this.leggingsItem;
            case TPMBoneIndents.BODY_ARMOR_BONE_INDENT,
                    TPMBoneIndents.RIGHT_ARM_ARMOR_BONE_INDENT,
                    TPMBoneIndents.LEFT_ARM_ARMOR_BONE_INDENT -> this.chestplateItem;
            case TPMBoneIndents.HEAD_ARMOR_BONE_INDENT -> this.helmetItem;
            default -> null;
        };
    }
    @Override
    protected EquipmentSlot getEquipmentSlotForArmorBone(@NotNull String boneName, TodePiglinMerchant currentEntity) {
        return switch (boneName) {
            case TPMBoneIndents.LEFT_FOOT_ARMOR_BONE_INDENT,
                    TPMBoneIndents.RIGHT_FOOT_ARMOR_BONE_INDENT -> EquipmentSlot.FEET;
            case TPMBoneIndents.LEFT_LEG_ARMOR_BONE_INDENT,
                    TPMBoneIndents.RIGHT_LEG_ARMOR_BONE_INDENT -> EquipmentSlot.LEGS;
            case TPMBoneIndents.RIGHT_ARM_ARMOR_BONE_INDENT -> !currentEntity.isLeftHanded() ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
            case TPMBoneIndents.LEFT_ARM_ARMOR_BONE_INDENT -> currentEntity.isLeftHanded() ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
            case TPMBoneIndents.BODY_ARMOR_BONE_INDENT -> EquipmentSlot.CHEST;
            case TPMBoneIndents.HEAD_ARMOR_BONE_INDENT -> EquipmentSlot.HEAD;
            default -> null;
        };
    }
    @Override
    protected ModelPart getArmorPartForBone(@NotNull String name, HumanoidModel<?> armorModel) {
        return switch (name) {
            case TPMBoneIndents.LEFT_FOOT_ARMOR_BONE_IDENT,
                    TPMBoneIndents.LEFT_LEG_ARMOR_BONE_IDENT -> armorModel.leftLeg;
            case TPMBoneIndents.RIGHT_FOOT_ARMOR_BONE_IDENT,
                    TPMBoneIndents.RIGHT_LEG_ARMOR_BONE_IDENT -> armorModel.rightLeg;
            case TPMBoneIndents.RIGHT_ARM_ARMOR_BONE_IDENT -> armorModel.rightArm;
            case TPMBoneIndents.LEFT_ARM_ARMOR_BONE_IDENT -> armorModel.leftArm;
            case TPMBoneIndents.BODY_ARMOR_BONE_IDENT -> armorModel.body;
            case TPMBoneIndents.HEAD_ARMOR_BONE_IDENT -> armorModel.head;
            default -> null;
        };
    }
    @Override
    protected ItemTransforms.TransformType getCameraTransformForItemAtBone(ItemStack boneItem, @NotNull String boneName) {
        return switch (boneName) {
            case TPMBoneIndents.LEFT_HAND_BONE_INDENT, TPMBoneIndents.RIGHT_HAND_BONE_INDENT -> 
                    ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND; // Do Defaults
            default -> ItemTransforms.TransformType.NONE;
        };
    }
    @Nullable
    @Override
    protected ItemStack getHeldItemForBone(@NotNull String boneName, TodePiglinMerchant animatable) {
        return switch (boneName) {
            case TPMBoneIndents.LEFT_HAND_BONE_INDENT -> animatable.isLeftHanded() ? mainHandItem : offHandItem;
            case TPMBoneIndents.RIGHT_HAND_BONE_INDENT -> animatable.isLeftHanded() ? offHandItem : mainHandItem;
            default -> null;
        };
    }
    @Override
    protected void preRenderItem(PoseStack poseStack, ItemStack itemStack, String boneName, TodePiglinMerchant animatable, IBone bone) {
        if (itemStack == this.mainHandItem) {
            poseStack.mulPose(Vector3f.XP.rotationDegrees(-90f));

            if (itemStack.getItem() instanceof ShieldItem)
                poseStack.translate(0, 0.125, -0.25);
        }
        else if (itemStack == this.offHandItem) {
            poseStack.mulPose(Vector3f.XP.rotationDegrees(-90f));

            if (itemStack.getItem() instanceof ShieldItem) {
                poseStack.translate(0, 0.125, 0.25);
                poseStack.mulPose(Vector3f.YP.rotationDegrees(180));
            }
        }
    }
    @Override
    protected void postRenderItem(PoseStack poseStack, ItemStack stack, String boneName, TodePiglinMerchant animatable, IBone bone) {}
    @Nullable
    @Override
    protected BlockState getHeldBlockForBone(String boneName, TodePiglinMerchant animatable) {
        return null;
    }
    @Override
    protected void preRenderBlock(PoseStack poseStack, BlockState state, String boneName, TodePiglinMerchant animatable) {}
    @Override
    protected void postRenderBlock(PoseStack poseStack, BlockState state, String boneName, TodePiglinMerchant animatable) {}
}

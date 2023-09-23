package com.github.warrentode.todepiglins.client.todepiglinmerchant;

import com.github.warrentode.todepiglins.entity.custom.todepiglinmerchant.TodePiglinMerchant;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import static com.github.warrentode.todepiglins.TodePiglins.MODID;

public class TodePiglinMerchantModel extends AnimatedGeoModel<TodePiglinMerchant> {
    @Override
    public void setCustomAnimations(TodePiglinMerchant animatable, int instanceId, AnimationEvent customPredicate) {
        super.setCustomAnimations(animatable, instanceId, customPredicate);
        float f = (Mth.PI / 6F);
        float f1 = (float)(customPredicate.animationTick * 0.1F + customPredicate.getLimbSwingAmount() * 0.5F);
        float f2 = (0.08F + customPredicate.getLimbSwingAmount() * 0.4F);

        IBone head = this.getAnimationProcessor().getBone("bipedHead");
        IBone leftEar = this.getAnimationProcessor().getBone("leftear");
        IBone rightEar = this.getAnimationProcessor().getBone("rightear");

        @SuppressWarnings("unchecked")
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);

        AnimationData manager = animatable.getFactory().getOrCreateAnimationData(instanceId);
        int unpausedMultiplier = !Minecraft.getInstance().isPaused() || manager.shouldPlayWhilePaused ? 1 : 0;

        head.setRotationX(head.getRotationX() + extraData.headPitch * ((float) Math.PI / 180F) * unpausedMultiplier);
        head.setRotationY(head.getRotationY() + extraData.netHeadYaw * ((float) Math.PI / 180F) * unpausedMultiplier);

        leftEar.setRotationZ(head.getRotationZ() + extraData.headPitch - f - Mth.cos(f1) * f2);
        rightEar.setRotationZ(head.getRotationZ() + extraData.headPitch + f  + Mth.cos(f1 * 1.2F) * f2);
    }

    @Override
    public ResourceLocation getModelResource(TodePiglinMerchant object) {
        return new ResourceLocation(MODID, "geo/entity/todepiglin/todepiglinmerchant.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TodePiglinMerchant object) {
        return new ResourceLocation(MODID, "textures/entity/todepiglin/todepiglinmerchant.png");
    }

    @Override
    public ResourceLocation getAnimationResource(TodePiglinMerchant animatable) {
        return new ResourceLocation(MODID, "animations/entity/todepiglin/todepiglinmerchant.animation.json");
    }
}
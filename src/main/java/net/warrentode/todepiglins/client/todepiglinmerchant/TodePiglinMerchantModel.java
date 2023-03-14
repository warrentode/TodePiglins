package net.warrentode.todepiglins.client.todepiglinmerchant;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.warrentode.todepiglins.entity.custom.todepiglinmerchant.TodePiglinMerchant;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import static net.warrentode.todepiglins.TodePiglins.MODID;

public class TodePiglinMerchantModel extends AnimatedGeoModel<TodePiglinMerchant> {
    @Override
    public void setCustomAnimations(TodePiglinMerchant animatable, int instanceId, AnimationEvent customPredicate) {
        super.setCustomAnimations(animatable, instanceId, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("bipedHead");
        IBone nose = this.getAnimationProcessor().getBone("noseBone");

        IBone headEmote = this.getAnimationProcessor().getBone("headParticles");
        IBone noseEmote = this.getAnimationProcessor().getBone("noseParticles");

        @SuppressWarnings("unchecked")
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);

        AnimationData manager = animatable.getFactory().getOrCreateAnimationData(instanceId);
        int unpausedMultiplier = !Minecraft.getInstance().isPaused() || manager.shouldPlayWhilePaused ? 1 : 0;

        head.setRotationX(head.getRotationX() + extraData.headPitch * ((float) Math.PI / 180F) * unpausedMultiplier);
        head.setRotationY(head.getRotationY() + extraData.netHeadYaw * ((float) Math.PI / 180F) * unpausedMultiplier);

        headEmote.setRotationX(headEmote.getRotationX() + extraData.headPitch * ((float) Math.PI / 180F) * unpausedMultiplier);
        headEmote.setRotationY(headEmote.getRotationY() + extraData.netHeadYaw * ((float) Math.PI / 180F) * unpausedMultiplier);

        nose.setPositionX(nose.getPositionX() + extraData.headPitch * ((float) Math.PI / 180F) * unpausedMultiplier);
        nose.setPositionY(nose.getPositionY() + extraData.netHeadYaw * ((float) Math.PI / 180F) * unpausedMultiplier);

        noseEmote.setPositionX(noseEmote.getPositionX() + extraData.headPitch * ((float) Math.PI / 180F) * unpausedMultiplier);
        noseEmote.setPositionY(noseEmote.getPositionY() + extraData.netHeadYaw * ((float) Math.PI / 180F) * unpausedMultiplier);
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

package net.warrentode.todepiglins.entity.custom.model.todepiglin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.warrentode.todepiglins.entity.custom.todepiglin.todepiglinmerchant.TodePiglinMerchant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import static net.warrentode.todepiglins.TodePiglins.MODID;

public class TodePiglinMerchantRenderer extends GeoEntityRenderer<TodePiglinMerchant> {
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
}

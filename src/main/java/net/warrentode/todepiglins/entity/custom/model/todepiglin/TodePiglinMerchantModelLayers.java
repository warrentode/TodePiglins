package net.warrentode.todepiglins.entity.custom.model.todepiglin;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

import static net.warrentode.todepiglins.TodePiglins.MODID;

public class TodePiglinMerchantModelLayers {
    public static final ModelLayerLocation TODEPIGLINMERCHANT =
            new ModelLayerLocation(new ResourceLocation(MODID, "todepiglin"), "main");
    public static final ModelLayerLocation TODEPIGLINMERCHANT_INNER_ARMOR =
            new ModelLayerLocation(new ResourceLocation(MODID, "todepiglin"), "inner_armor");
    public static final ModelLayerLocation TODEPIGLINMERCHANT_OUTER_ARMOR =
            new ModelLayerLocation(new ResourceLocation(MODID, "todepiglin"), "inner_armor");
}

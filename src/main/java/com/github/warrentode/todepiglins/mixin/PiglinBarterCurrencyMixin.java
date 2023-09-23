package com.github.warrentode.todepiglins.mixin;

import com.github.warrentode.todepiglins.util.ModTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.world.entity.monster.piglin.PiglinAi.*;

@Mixin(PiglinAi.class)
public abstract class PiglinBarterCurrencyMixin {
    @Inject(at = @At("HEAD"), method = "stopHoldingOffHandItem", cancellable = true)
    private static void todepiglins_stopHoldingOffHandItem(@NotNull Piglin pPiglin, boolean pShouldBarter, CallbackInfo ci) {
        if (pPiglin.isAdult()) {
            if (pPiglin.getItemInHand(InteractionHand.OFF_HAND).is(ModTags.Items.PIGLIN_BARTER_ITEMS)) {
                pPiglin.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
                throwItems(pPiglin, getBarterResponseItems(pPiglin));
                ci.cancel();
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "wantsToPickup", cancellable = true)
    private static void todepiglins_wantsToPickup(Piglin pPiglin, @NotNull ItemStack pStack, CallbackInfoReturnable<Boolean> cir) {
        if (pStack.is(ModTags.Items.PIGLIN_BARTER_ITEMS) && isNotHoldingLovedItemInOffHand(pPiglin)) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "canAdmire", cancellable = true)
    private static void todepiglins_todepiglins_canAdmire(Piglin pPiglin, ItemStack pStack, CallbackInfoReturnable<Boolean> cir) {
        if (!isAdmiringDisabled(pPiglin) && !isAdmiringItem(pPiglin) && pPiglin.isAdult() && (pStack.isPiglinCurrency() || pStack.is(ModTags.Items.PIGLIN_BARTER_ITEMS))) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "isBarterCurrency", cancellable = true)
    private static void todepiglins_isBarterCurrency(@NotNull ItemStack pStack, CallbackInfoReturnable<Boolean> cir) {
        if (pStack.is(ModTags.Items.PIGLIN_BARTER_ITEMS)) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
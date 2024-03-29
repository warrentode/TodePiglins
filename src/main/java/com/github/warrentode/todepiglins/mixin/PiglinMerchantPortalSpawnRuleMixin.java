package com.github.warrentode.todepiglins.mixin;

import com.github.warrentode.todepiglins.entity.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetherPortalBlock.class)
public abstract class PiglinMerchantPortalSpawnRuleMixin {
    @SuppressWarnings("CanBeFinal")
    @Unique
    Block todePiglins$pBlock = (Block) (Object) this;

    @Inject(at = @At("HEAD"), method = "randomTick")
    private void todepiglins_randomTick(BlockState pState, @NotNull ServerLevel pLevel, BlockPos pPos, RandomSource pRandom, CallbackInfo ci) {
        if (pLevel.dimensionType().natural() && pLevel.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && pRandom.nextInt(1000) < pLevel.getDifficulty().getId()) {
            while(pLevel.getBlockState(pPos).is(todePiglins$pBlock)) {
                pPos = pPos.below();
            }

            if (pLevel.getBlockState(pPos).isValidSpawn(pLevel, pPos, ModEntityTypes.TODEPIGLINMERCHANT.get())) {
                Entity entity = ModEntityTypes.TODEPIGLINMERCHANT.get().spawn(pLevel, null, null, null, pPos.above(), MobSpawnType.STRUCTURE, false, false);
                if (entity != null) {
                    entity.setPortalCooldown();
                }
            }
        }
    }
}
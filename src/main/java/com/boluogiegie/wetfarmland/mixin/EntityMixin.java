package com.boluogiegie.wetfarmland.mixin;

import com.boluogiegie.wetfarmland.config.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "checkFallDamage", at = @At("HEAD"))
    private void onCheckFallDamage(double y, boolean onGround, BlockState state, BlockPos pos, CallbackInfo ci) {
        Entity entity = (Entity)(Object)this;
        Level level = entity.level();

        if (!level.isClientSide && Config.isTramplingPrevented()) {
            Block block = state.getBlock();
            if (block instanceof FarmBlock) {
            }
        }
    }
}
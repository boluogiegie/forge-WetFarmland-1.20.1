package com.boluogiegie.wetfarmland.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FarmBlock.class)
public abstract class FarmlandSolidCheckMixin {

    @Inject(method = "canSurvive", at = @At("HEAD"), cancellable = true)
    private void onCanSurviveHead(BlockState state, LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BlockState aboveState = level.getBlockState(pos.above());
        if (!aboveState.isAir() && aboveState.isSolidRender(level, pos.above())) {
            Block aboveBlock = aboveState.getBlock();

            if (!(aboveBlock instanceof net.minecraft.world.level.block.FenceGateBlock) &&
                    !(aboveBlock instanceof net.minecraft.world.level.block.piston.MovingPistonBlock)) {
                cir.setReturnValue(false);
                return;
            }
        }

    }
}
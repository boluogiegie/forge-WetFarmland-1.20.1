package com.boluogiegie.wetfarmland.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FarmBlock.class)
public abstract class FarmlandUpdateMixin {

    @Inject(method = "updateShape", at = @At("HEAD"), cancellable = true)
    private void onUpdateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {
        if (direction == Direction.UP && neighborPos.equals(pos.above())) {
            if (!neighborState.isAir() && neighborState.isSolidRender(level, neighborPos)) {
                return;
            }
        }
    }
}
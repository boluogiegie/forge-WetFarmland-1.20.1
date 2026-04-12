package com.boluogiegie.wetfarmland.mixin;

import com.boluogiegie.wetfarmland.config.Config;
import com.boluogiegie.wetfarmland.data.FarmlandData;
import com.boluogiegie.wetfarmland.data.FarmlandDataManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(FarmBlock.class)
public abstract class FarmlandBlockMixin extends Block {

    @Shadow @Final
    public static IntegerProperty MOISTURE;

    protected FarmlandBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void onRandomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (!level.isClientSide) {
            int currentMoisture = state.getValue(MOISTURE);
            long currentDay = level.getDayTime() / 24000L;
            FarmlandDataManager dataManager = FarmlandDataManager.getInstance();
            FarmlandData data = dataManager.getData(pos, level);

            if (data == null) {
                data = new FarmlandData(currentDay);
                if (currentMoisture == 0) {
                    data.wasEverWet = false;
                    //data.updateLastDry(currentDay);
                    dataManager.setData(pos, level, data);
                    long daysDry = data.getDaysSinceLastDry(currentDay);
                    if (daysDry >= Config.getHardenPeriod()) {
                        turnToDirt(null, state, level, pos);
                    }
                    ci.cancel();
                    return;
                } else if (currentMoisture == 7) {
                    data.wasEverWet = true;
                    data.updateWet(currentDay);
                    data.updateDryStart(currentDay);
                    dataManager.setData(pos, level, data);
                }
            }
            boolean isNearWater = isNearWaterOriginal(level, pos);
            boolean isRaining = level.isRainingAt(pos.above());
            if (isNearWater || isRaining) {
                if (currentMoisture < 7) {
                    level.setBlock(pos, state.setValue(MOISTURE, 7), 2);
                }
                data.updateWet(currentDay);
                data.updateDryStart(currentDay);
                data.updateLastDry(currentDay);
                dataManager.setData(pos, level, data);
                ci.cancel();
                return;
            }
            //没有水
            long daysSinceWet = data.getDaysSinceWet(currentDay);
            if (!data.wasEverWet) {
                long daysDry = data.getDaysSinceLastDry(currentDay);
                if (daysDry >= Config.getHardenPeriod()) {
                    turnToDirt(null, state, level, pos);
                }
                ci.cancel();
                return;
            }

            //曾湿润过的耕地
            if (daysSinceWet < Config.getWetPeriod()) {
                if (currentMoisture < 7) {
                    level.setBlock(pos, state.setValue(MOISTURE, 7), 2);
                }
                data.updateDryStart(currentDay);
                dataManager.setData(pos, level, data);
                ci.cancel();
                return;
            }
            //开始干燥
            long daysAfterWetPeriod = daysSinceWet - Config.getWetPeriod();
            int targetMoisture = 6 - (int) daysAfterWetPeriod;
            if (targetMoisture < 0) {
                targetMoisture = 0;
            }
            if (currentMoisture != targetMoisture) {
                level.setBlock(pos, state.setValue(MOISTURE, targetMoisture), 2);
                currentMoisture = targetMoisture;
                if (currentMoisture == 6 && data.getDaysSinceLastDry(currentDay) <= 0) {
                    data.updateLastDry(currentDay);
                }
            }

            if (currentMoisture <= 6) {
                long daysSinceLastDry = data.getDaysSinceLastDry(currentDay);

                if (daysSinceLastDry == 0 && currentMoisture == 6) {
                    data.updateLastDry(currentDay);
                    daysSinceLastDry = 1;
                }

                if (daysSinceLastDry >= Config.getHardenPeriod()) {
                    turnToDirt(null, state, level, pos);
                }
            }
            dataManager.setData(pos, level, data);
            ci.cancel();
        }
    }

    @Unique
    private static void hardenFarmland(ServerLevel level, BlockPos pos, BlockState state) {
        BlockState aboveState = level.getBlockState(pos.above());
        Block aboveBlock = aboveState.getBlock();

        if (aboveBlock instanceof net.minecraft.world.level.block.CropBlock ||  // 小麦、胡萝卜、土豆等
                aboveBlock instanceof net.minecraft.world.level.block.StemBlock ||  // 南瓜/西瓜
                aboveBlock instanceof net.minecraft.world.level.block.AttachedStemBlock ||
                aboveBlock instanceof net.minecraft.world.level.block.NetherWartBlock ||
                aboveBlock instanceof net.minecraft.world.level.block.CocoaBlock ||
                aboveBlock instanceof net.minecraft.world.level.block.BeetrootBlock) {  // 甜菜
            level.destroyBlock(pos.above(), false);
        }
        BlockState blockstate = Block.pushEntitiesUp(state, Blocks.DIRT.defaultBlockState(), level, pos);
        level.setBlockAndUpdate(pos, blockstate);
        level.gameEvent(net.minecraft.world.level.gameevent.GameEvent.BLOCK_CHANGE, pos, net.minecraft.world.level.gameevent.GameEvent.Context.of(null, blockstate));
        FarmlandDataManager.getInstance().removeData(pos, level);
    }

    @Shadow
    public static void turnToDirt(@Nullable Entity entity, BlockState state, Level level, BlockPos pos) {}

    @Unique
    private static boolean isNearWaterOriginal(LevelReader level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        for(BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 1, 4))) {
            if (state.canBeHydrated(level, pos, level.getFluidState(blockpos), blockpos)) {
                return true;
            }
        }

        for(BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 0, 4))) {
            if (level.getFluidState(blockpos).is(net.minecraft.tags.FluidTags.WATER)) {
                return true;
            }
        }

        return false;
    }

    @Inject(method = "turnToDirt", at = @At("HEAD"), cancellable = true)
    private static void onTurnToDirt(@Nullable Entity entity, BlockState state, Level level, BlockPos pos, CallbackInfo ci) {
        if (!level.isClientSide && level instanceof ServerLevel && Config.isHardenEnabled()) {
            ServerLevel serverLevel = (ServerLevel) level;
            int moisture = 0;
            if (state.getBlock() instanceof FarmBlock) {
                moisture = state.getValue(MOISTURE);
            }

            FarmlandDataManager dataManager = FarmlandDataManager.getInstance();
            FarmlandData data = dataManager.getData(pos, serverLevel);

            if (data != null) {
                long currentDay = serverLevel.getDayTime() / 24000L;
                long daysDry = data.getDaysSinceLastDry(currentDay);

                boolean isNeverWetAndDryLongEnough = !data.wasEverWet && daysDry >= Config.getHardenPeriod();
                boolean isDriedLongEnough = data.wasEverWet && moisture <= 6 && daysDry >= Config.getHardenPeriod();

                if (isNeverWetAndDryLongEnough || isDriedLongEnough) {
                    hardenFarmland(serverLevel, pos, state);
                    ci.cancel();
                    return;
                }
            }

            if (Config.isHardenEnabled() && moisture == 0) {
                BlockState aboveState = level.getBlockState(pos.above());
                if (aboveState.isAir() || !aboveState.isSolidRender(level, pos.above())) {
                    ci.cancel();
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (Config.isHardenEnabled()) {
            int moisture = state.getValue(MOISTURE);
            if (moisture == 0) {
                BlockState aboveState = level.getBlockState(pos.above());
                if (aboveState.isAir() || !aboveState.isSolidRender(level, pos.above())) {
                    ci.cancel();
                }
            }
        }
    }

    @Inject(method = "canSurvive", at = @At("RETURN"), cancellable = true)
    private void onCanSurvive(BlockState state, LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            BlockState aboveState = level.getBlockState(pos.above());
            Block aboveBlock = aboveState.getBlock();

            if (aboveBlock instanceof net.minecraft.world.level.block.CropBlock || aboveBlock instanceof net.minecraft.world.level.block.StemBlock || aboveBlock instanceof net.minecraft.world.level.block.AttachedStemBlock || aboveBlock instanceof net.minecraft.world.level.block.NetherWartBlock || aboveBlock instanceof net.minecraft.world.level.block.CocoaBlock || aboveBlock instanceof net.minecraft.world.level.block.BeetrootBlock || aboveBlock instanceof net.minecraft.world.level.block.SaplingBlock || aboveBlock instanceof net.minecraft.world.level.block.MushroomBlock) {
                if (!aboveState.isSolidRender(level, pos.above())) {
                    cir.setReturnValue(true);
                    return;
                }
            }
        }
    }
}
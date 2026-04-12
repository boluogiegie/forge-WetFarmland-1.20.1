package com.boluogiegie.wetfarmland.event;

import com.boluogiegie.wetfarmland.config.Config;
import com.boluogiegie.wetfarmland.data.FarmlandData;
import com.boluogiegie.wetfarmland.data.FarmlandDataManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber
public class FarmlandEventHandler {

    @SubscribeEvent
    public static void onBlockToolModification(BlockEvent.BlockToolModificationEvent event) {
        if (!event.getLevel().isClientSide() && Config.isHardenEnabled()) {
            BlockState finalState = event.getFinalState();
            if (finalState != null && finalState.getBlock() instanceof FarmBlock) {
                ServerLevel level = (ServerLevel) event.getLevel();
                BlockPos pos = event.getPos();
                FarmlandDataManager dataManager = FarmlandDataManager.getInstance();
                dataManager.removeData(pos, level);
                long currentDay = level.getDayTime() / 24000L;
                FarmlandData data = new FarmlandData(currentDay);
                data.wasEverWet = false;
                data.updateLastDry(currentDay);
                dataManager.setData(pos, level, data);
            }
        }
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!event.getLevel().isClientSide()) {
            BlockState placedState = event.getPlacedBlock();
            BlockPos pos = event.getPos();
            BlockPos belowPos = pos.below();
            BlockState belowState = event.getLevel().getBlockState(belowPos);

            if (belowState.getBlock() instanceof FarmBlock) {
                if (!placedState.isAir() && placedState.isSolidRender(event.getLevel(), pos)) {
                    ServerLevel level = (ServerLevel) event.getLevel();
                    FarmlandDataManager.getInstance().removeData(belowPos, level);
                    try {
                        FarmBlock.turnToDirt(null, belowState, level, belowPos);
                    } catch (Exception e) {
                        level.setBlock(belowPos, net.minecraft.world.level.block.Blocks.DIRT.defaultBlockState(), 3);
                    }
                }
            }

            if (Config.isHardenEnabled() && placedState.getBlock() instanceof FarmBlock) {
                ServerLevel level = (ServerLevel) event.getLevel();
                FarmlandDataManager dataManager = FarmlandDataManager.getInstance();
                dataManager.removeData(pos, level);
                long currentDay = level.getDayTime() / 24000L;
                FarmlandData data = new FarmlandData(currentDay);
                int moisture = placedState.getValue(FarmBlock.MOISTURE);
                if (moisture == 0) {
                    data.wasEverWet = false;
                    data.updateLastDry(currentDay);
                } else if (moisture == 7) {
                    // 放置已经湿润的耕地
                    data.wasEverWet = true;
                    data.updateWet(currentDay);
                    data.updateDryStart(currentDay);
                }
                dataManager.setData(pos, level, data);
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!event.getLevel().isClientSide()) {
            BlockState state = event.getState();
            if (state.getBlock() instanceof FarmBlock) {
                FarmlandDataManager.getInstance().removeData(event.getPos(), (ServerLevel) event.getLevel());
            }
        }
    }

    @SubscribeEvent
    public static void onBlockUpdate(BlockEvent.NeighborNotifyEvent event) {
        if (!event.getLevel().isClientSide()) {
            ServerLevel level = (ServerLevel) event.getLevel();
            BlockPos pos = event.getPos();
            BlockState state = level.getBlockState(pos);

            if (state.getBlock() instanceof FarmBlock) {
                BlockState aboveState = level.getBlockState(pos.above());
                if (!aboveState.isAir() && aboveState.isSolidRender(level, pos.above())) {
                    Block aboveBlock = aboveState.getBlock();
                    if (!(aboveBlock instanceof net.minecraft.world.level.block.FenceGateBlock) && !(aboveBlock instanceof net.minecraft.world.level.block.piston.MovingPistonBlock)) {
                        FarmlandDataManager.getInstance().removeData(pos, level);
                        try {
                            FarmBlock.turnToDirt(null, state, level, pos);
                        } catch (Exception e) {
                            level.setBlock(pos, net.minecraft.world.level.block.Blocks.DIRT.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onFarmlandTrample(BlockEvent.FarmlandTrampleEvent event) {
        if (Config.isTramplingPrevented()) {
            event.setCanceled(true);
        }
    }
}
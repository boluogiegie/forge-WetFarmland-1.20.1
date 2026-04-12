package com.boluogiegie.wetfarmland.event;

import com.boluogiegie.wetfarmland.config.Config;
import com.boluogiegie.wetfarmland.data.FarmlandData;
import com.boluogiegie.wetfarmland.data.FarmlandDataManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EventHandler {

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!event.getLevel().isClientSide() && Config.isHardenEnabled()) {
            BlockState placedState = event.getPlacedBlock();
            BlockPos pos = event.getPos();
            BlockPos belowPos = pos.below();
            BlockState belowState = event.getLevel().getBlockState(belowPos);

            if (belowState.getBlock() instanceof FarmBlock) {
                FarmlandDataManager.getInstance().removeData(belowPos, (ServerLevel) event.getLevel());
            }

            if (placedState.getBlock() instanceof FarmBlock) {
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
                    data.wasEverWet = true;
                    data.updateWet(currentDay);
                    data.updateDryStart(currentDay);
                }

                dataManager.setData(pos, level, data);
            }
        }
    }

    @SubscribeEvent
    public static void onBlockUpdate(BlockEvent.NeighborNotifyEvent event) {
        if (Config.isHardenEnabled() && !event.getLevel().isClientSide()) {
            ServerLevel level = (ServerLevel) event.getLevel();
            BlockPos pos = event.getPos();
            BlockState state = level.getBlockState(pos);

            if (state.getBlock() instanceof FarmBlock) {
                BlockState aboveState = level.getBlockState(pos.above());
                if (!aboveState.isAir() && aboveState.isSolidRender(level, pos.above())) {
                    FarmlandDataManager.getInstance().removeData(pos, level);
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
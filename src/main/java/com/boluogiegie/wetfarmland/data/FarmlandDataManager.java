package com.boluogiegie.wetfarmland.data;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FarmlandDataManager {
    private static final FarmlandDataManager INSTANCE = new FarmlandDataManager();
    private final Map<String, FarmlandData> farmlandDataMap = new HashMap<>();

    private FarmlandDataManager() {}

    public static FarmlandDataManager getInstance() {
        return INSTANCE;
    }

    private String getKey(BlockPos pos, Level level) {
        return level.dimension().location().toString() + ":" + pos.toShortString();
    }

    public FarmlandData getData(BlockPos pos, Level level) {
        String key = getKey(pos, level);
        FarmlandData data = farmlandDataMap.get(key);

        if (data != null) {
            BlockState state = level.getBlockState(pos);
            if (!(state.getBlock() instanceof net.minecraft.world.level.block.FarmBlock)) {
                farmlandDataMap.remove(key);
                return null;
            }
        }
        return data;
    }

    public void setData(BlockPos pos, Level level, FarmlandData data) {
        String key = getKey(pos, level);
        farmlandDataMap.put(key, data);
    }

    public void removeData(BlockPos pos, Level level) {
        String key = getKey(pos, level);
        farmlandDataMap.remove(key);
    }

    public int getDataCount() {
        return farmlandDataMap.size();
    }

    public void clearAllData() {
        farmlandDataMap.clear();
    }

    public void validateAndFixData(BlockPos pos, Level level, int moisture) {
        String key = getKey(pos, level);
        FarmlandData data = farmlandDataMap.get(key);
        if (data != null) {
            long currentDay = getCurrentDay(level);
            if (moisture == 0 && !data.wasEverWet && data.getDaysSinceLastDry(currentDay) <= 0) {
                data.updateLastDry(currentDay);
                farmlandDataMap.put(key, data);
            }
        }
    }

    public void cleanupInvalidData(Level level) {
        Iterator<Map.Entry<String, FarmlandData>> iterator = farmlandDataMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, FarmlandData> entry = iterator.next();
            String key = entry.getKey();
            try {
                String[] parts = key.split(":");
                if (parts.length >= 3) {
                    String dimension = parts[0] + ":" + parts[1];
                    String posStr = parts[2];
                    if (!level.dimension().location().toString().equals(dimension)) {
                        continue;
                    }
                    String[] coords = posStr.split(",");
                    int x = Integer.parseInt(coords[0]);
                    int y = Integer.parseInt(coords[1]);
                    int z = Integer.parseInt(coords[2]);
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    if (!(state.getBlock() instanceof net.minecraft.world.level.block.FarmBlock)) {
                        iterator.remove();
                    }
                }
            } catch (Exception e) {
                iterator.remove();
            }
        }
    }

    public long getCurrentDay(Level level) {
        return level.getDayTime() / 24000L;
    }
}
package com.boluogiegie.wetfarmland.data;

public class FarmlandData {
    public long lastWetDay;      // 上次变湿的游戏天数
    public long dryStartDay;     // 开始干燥的游戏天数
    public long lastDryDay;      // 开始硬化的游戏天数
    public boolean wasEverWet;   // 是否曾经湿润过

    public FarmlandData(long currentDay) {
        this.lastWetDay = currentDay;
        this.dryStartDay = currentDay;
        this.lastDryDay = currentDay;
        this.wasEverWet = false;
    }

    public void updateWet(long currentDay) {
        this.lastWetDay = currentDay;
        this.wasEverWet = true;
    }

    public void updateDryStart(long currentDay) {
        this.dryStartDay = currentDay;
    }

    public void updateLastDry(long currentDay) {
        this.lastDryDay = currentDay;
    }

    public long getDaysSinceWet(long currentDay) {
        return currentDay - lastWetDay;
    }

    public long getDaysSinceDryStart(long currentDay) {
        return currentDay - dryStartDay;
    }

    public long getDaysSinceLastDry(long currentDay) {
        return currentDay - lastDryDay;
    }
}
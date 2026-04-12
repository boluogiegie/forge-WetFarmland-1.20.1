package com.boluogiegie.wetfarmland;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(WetFarmlandMod.MOD_ID)
public class WetFarmlandMod {
    public static final String MOD_ID = "wetfarmland";
    public static final Logger LOGGER = LogManager.getLogger();
    public WetFarmlandMod(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::setup);
        modContainer.registerConfig(ModConfig.Type.COMMON, com.boluogiegie.wetfarmland.config.Config.SPEC);
    }
    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Wet Farmland Mod initialized!");
    }
}
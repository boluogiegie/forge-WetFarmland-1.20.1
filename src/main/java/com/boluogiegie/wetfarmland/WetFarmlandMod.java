package com.boluogiegie.wetfarmland;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(WetFarmlandMod.MOD_ID)
public class WetFarmlandMod {
    public static final String MOD_ID = "wetfarmland";
    public static final Logger LOGGER = LogManager.getLogger();

    public WetFarmlandMod(FMLJavaModLoadingContext context) {
        context.getModEventBus().addListener(this::setup);
        context.registerConfig(ModConfig.Type.COMMON, com.boluogiegie.wetfarmland.config.Config.SPEC, MOD_ID + ".toml");
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Wet Farmland Mod initialized!");
    }
}
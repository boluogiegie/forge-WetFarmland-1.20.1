package com.boluogiegie.wetfarmland.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Config {
    public static final Common COMMON;
    public static final ModConfigSpec SPEC;

    static {
        final Pair<Common, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Common::new);
        COMMON = specPair.getLeft();
        SPEC = specPair.getRight();
    }

    public static class Common {
        public final ModConfigSpec.IntValue wetPeriod;
        public final ModConfigSpec.IntValue hardenPeriod;
        public final ModConfigSpec.BooleanValue enableHarden;
        public final ModConfigSpec.BooleanValue enableTrampling;
        public final ModConfigSpec.BooleanValue destroyCropsOnHarden;

        public Common(ModConfigSpec.Builder builder) {
            builder.comment("Wet Farmland Configuration")
                    .push("general");

            wetPeriod = builder
                    .comment("Number of in-game days farmland stays wet (default: 4)")
                    .defineInRange("wetPeriod", 4, 1, 365);

            hardenPeriod = builder
                    .comment("Number of in-game days dry farmland hardens to dirt (default: 30)")
                    .defineInRange("hardenPeriod", 30, 1, 365);

            enableHarden = builder
                    .comment("Enable farmland hardening to dirt")
                    .define("enableHarden", true);

            enableTrampling = builder
                    .comment("Prevent farmland from being trampled")
                    .define("enableTrampling", true);

            destroyCropsOnHarden = builder
                    .comment("Destroy crops when farmland hardens")
                    .define("destroyCropsOnHarden", true);

            builder.pop();
        }
    }

    public static int getWetPeriod() {
        return COMMON.wetPeriod.get();
    }

    public static int getHardenPeriod() {
        return COMMON.hardenPeriod.get();
    }

    public static boolean isHardenEnabled() {
        return COMMON.enableHarden.get();
    }

    public static boolean isTramplingPrevented() {
        return COMMON.enableTrampling.get();
    }

    public static boolean shouldDestroyCropsOnHarden() {
        return COMMON.destroyCropsOnHarden.get();
    }
}
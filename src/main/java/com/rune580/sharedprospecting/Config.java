package com.rune580.sharedprospecting;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class Config {

    public static int maxSyncPacketSizeInBytes = 30000;

    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);

        maxSyncPacketSizeInBytes = configuration.getInt(
            "MaxSyncPacketSizeInBytes",
            Configuration.CATEGORY_GENERAL,
            maxSyncPacketSizeInBytes,
            1000,
            30000,
            "");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}

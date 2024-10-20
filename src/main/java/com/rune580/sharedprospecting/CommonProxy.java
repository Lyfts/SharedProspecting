package com.rune580.sharedprospecting;

import net.minecraftforge.common.MinecraftForge;

import com.rune580.sharedprospecting.database.TeamsCache;
import com.rune580.sharedprospecting.hooks.HooksEventBus;
import com.rune580.sharedprospecting.hooks.HooksFML;
import com.rune580.sharedprospecting.networking.ProspectionSyncMsg;
import com.rune580.sharedprospecting.networking.SPNetwork;
import com.rune580.sharedprospecting.networking.SyncMsg;
import com.rune580.sharedprospecting.worker.batch.BatchWorker;
import com.rune580.sharedprospecting.worker.batch.TeamSyncBatchWorker;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy {

    public BatchWorker batchWorker;

    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());

        SPNetwork.registerMessage(ProspectionSyncMsg.ServerHandler.class, ProspectionSyncMsg.class, Side.SERVER);
        SPNetwork.registerMessage(ProspectionSyncMsg.ClientHandler.class, ProspectionSyncMsg.class, Side.CLIENT);
        SPNetwork.registerMessage(SyncMsg.ServerHandler.class, SyncMsg.class, Side.SERVER);
        SPNetwork.registerMessage(SyncMsg.ClientHandler.class, SyncMsg.class, Side.CLIENT);

        batchWorker = new BatchWorker();
        batchWorker.addBatchWork(new TeamSyncBatchWorker());
    }

    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new HooksEventBus());
        FMLCommonHandler.instance()
            .bus()
            .register(new HooksFML());
    }

    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {}

    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {}

    public void serverStopping(FMLServerStoppingEvent event) {
        TeamsCache.instance.save();
        TeamsCache.instance.reset();
    }
}

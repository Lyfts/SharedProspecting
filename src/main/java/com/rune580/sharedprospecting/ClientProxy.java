package com.rune580.sharedprospecting;

import net.minecraftforge.common.MinecraftForge;

import com.rune580.sharedprospecting.hooks.ClientEventHandler;
import com.rune580.sharedprospecting.worker.batch.ClientSyncBatchWork;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        batchWorker.addBatchWork(new ClientSyncBatchWork());
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        FMLCommonHandler.instance()
            .bus()
            .register(new ClientEventHandler());
    }
}

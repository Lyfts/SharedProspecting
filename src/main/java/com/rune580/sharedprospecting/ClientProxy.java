package com.rune580.sharedprospecting;

import com.rune580.sharedprospecting.worker.batch.ClientSyncBatchWork;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        batchWorker.addBatchWork(new ClientSyncBatchWork());
    }
}

package com.rune580.sharedprospecting.hooks;

import com.rune580.sharedprospecting.SharedProspectingMod;
import com.rune580.sharedprospecting.worker.TickWorker;
import com.rune580.sharedprospecting.worker.batch.ClientSyncBatchWork;
import com.sinthoras.visualprospecting.hooks.ProspectingNotificationEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class ClientEventHandler {

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        TickWorker.instance.onTick();
        SharedProspectingMod.proxy.batchWorker.onTick();
    }

    @SubscribeEvent
    public void onProspectingOreNotificationEvent(ProspectingNotificationEvent.OreVein event) {
        ClientSyncBatchWork.instance.addOreVein(event.getPosition());
    }

    @SubscribeEvent
    public void OnProspectingFluidNotificationEvent(ProspectingNotificationEvent.UndergroundFluid event) {
        ClientSyncBatchWork.instance.addUndergroundFluid(event.getPosition());
    }
}

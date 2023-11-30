package com.rune580.sharedprospecting.worker;

import com.rune580.sharedprospecting.SharedProspectingMod;
import com.rune580.sharedprospecting.networking.ProspectionSyncMsg;
import com.rune580.sharedprospecting.networking.SPNetwork;
import com.rune580.sharedprospecting.networking.SyncMsg;
import com.sinthoras.visualprospecting.database.ClientCache;

public class ClientToServerFullSync extends SyncWork {
    public ClientToServerFullSync() {
        oreVeins = ClientCache.instance.getAllOreVeins();
        undergroundFluids = ClientCache.instance.getAllUndergroundFluids();

        SharedProspectingMod.LOG.info("Starting sync Client -> Server");
    }

    @Override
    protected void sendSync() {
        final ProspectionSyncMsg packet = new ProspectionSyncMsg();

        final int oresConsumed = packet.addOreVeins(oreVeins);
        oreVeins.subList(0, oresConsumed).clear();

        final int fluidsConsumed = packet.addUndergroundFluids(undergroundFluids);
        undergroundFluids.subList(0, fluidsConsumed).clear();

        SPNetwork.sendToServer(packet);
    }

    @Override
    protected void onFinished() {
        SyncMsg packet = new SyncMsg();
        packet.setFullFinishedSync(true);

        SPNetwork.sendToServer(packet);
    }
}

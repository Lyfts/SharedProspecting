package com.rune580.sharedprospecting.worker;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayerMP;

import com.rune580.sharedprospecting.SharedProspectingMod;
import com.rune580.sharedprospecting.database.TeamCache;
import com.rune580.sharedprospecting.database.TeamsCache;
import com.rune580.sharedprospecting.networking.ProspectionSyncMsg;
import com.rune580.sharedprospecting.networking.SPNetwork;

public class ServerToSingleClientSync extends SyncWork {

    private EntityPlayerMP target;

    public ServerToSingleClientSync(EntityPlayerMP player) {
        TeamCache teamCache = TeamsCache.instance.getByPlayer(player);
        if (teamCache == null) {
            SharedProspectingMod.LOG.warn("Can't sync to player as they don't belong to a team!");

            oreVeins = new ArrayList<>();
            undergroundFluids = new ArrayList<>();

            return;
        }

        target = player;

        oreVeins = teamCache.getAllOreVeins();
        undergroundFluids = teamCache.getAllUndergroundFluids();

        SharedProspectingMod.LOG.info("Starting sync Server -> Single Client");
    }

    @Override
    protected void sendSync() {
        if (oreVeins.isEmpty() && undergroundFluids.isEmpty()) {
            return;
        }

        final ProspectionSyncMsg packet = new ProspectionSyncMsg();
        final int oresConsumed = packet.addOreVeins(oreVeins);
        oreVeins.subList(0, oresConsumed)
            .clear();

        final int fluidsConsumed = packet.addUndergroundFluids(undergroundFluids);
        undergroundFluids.subList(0, fluidsConsumed)
            .clear();

        SPNetwork.sendToPlayer(packet, target);
    }
}

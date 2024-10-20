package com.rune580.sharedprospecting.worker.batch;

import java.util.ArrayList;
import java.util.List;

import com.rune580.sharedprospecting.networking.ProspectionSyncMsg;
import com.rune580.sharedprospecting.networking.SPNetwork;
import com.sinthoras.visualprospecting.database.OreVeinPosition;
import com.sinthoras.visualprospecting.database.UndergroundFluidPosition;

public class ClientSyncBatchWork extends BatchWorkBase {

    public static ClientSyncBatchWork instance;
    private final List<OreVeinPosition> oreVeins = new ArrayList<>();
    private final List<UndergroundFluidPosition> undergroundFluids = new ArrayList<>();

    public ClientSyncBatchWork() {
        super(1000);

        instance = this;
    }

    public void addOreVein(OreVeinPosition oreVein) {
        oreVeins.add(oreVein);
    }

    public void addUndergroundFluid(UndergroundFluidPosition undergroundFluid) {
        undergroundFluids.add(undergroundFluid);
    }

    @Override
    protected void run() {
        if(oreVeins.isEmpty() && undergroundFluids.isEmpty()) return;
        final ProspectionSyncMsg packet = new ProspectionSyncMsg();

        final int oresConsumed = packet.addOreVeins(oreVeins);
        oreVeins.subList(0, oresConsumed)
            .clear();

        final int fluidsConsumed = packet.addUndergroundFluids(undergroundFluids);
        undergroundFluids.subList(0, fluidsConsumed)
            .clear();

        SPNetwork.sendToServer(packet);
    }
}

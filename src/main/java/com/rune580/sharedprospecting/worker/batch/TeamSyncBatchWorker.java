package com.rune580.sharedprospecting.worker.batch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rune580.sharedprospecting.networking.ProspectionSyncMsg;
import com.rune580.sharedprospecting.networking.SPNetwork;
import com.sinthoras.visualprospecting.database.OreVeinPosition;
import com.sinthoras.visualprospecting.database.UndergroundFluidPosition;

import serverutils.lib.data.ForgeTeam;

public class TeamSyncBatchWorker extends BatchWorkBase {

    public static TeamSyncBatchWorker instance;
    private final Map<String, TeamBatchData> teamBatchMap = new HashMap<>();

    public TeamSyncBatchWorker() {
        super(1000);

        instance = this;
    }

    public void addOreVeins(ForgeTeam team, List<OreVeinPosition> oreVeins) {
        getTeamBatchData(team).addOreVeins(oreVeins);
    }

    public void addUndergroundFluids(ForgeTeam team, List<UndergroundFluidPosition> undergroundFluids) {
        getTeamBatchData(team).addUndergroundFluids(undergroundFluids);
    }

    private TeamBatchData getTeamBatchData(ForgeTeam team) {
        return teamBatchMap.computeIfAbsent(team.getUIDCode(), (_key) -> new TeamBatchData(team));
    }

    @Override
    protected void run() {
        if(teamBatchMap.isEmpty()) return;
        List<String> entriesToRemove = new ArrayList<>();

        for(Map.Entry<String, TeamBatchData> entry : teamBatchMap.entrySet()) {
            if(entry.getValue().sendData()) {
                entriesToRemove.add(entry.getKey());
            }
        }

        for (String key : entriesToRemove) {
            teamBatchMap.remove(key);
        }
    }

    public static class TeamBatchData {

        private final ForgeTeam team;
        private final List<OreVeinPosition> oreVeins = new ArrayList<>();
        private final List<UndergroundFluidPosition> undergroundFluids = new ArrayList<>();

        private TeamBatchData(ForgeTeam team) {
            this.team = team;
        }

        private void addOreVeins(List<OreVeinPosition> oreVeins) {
            this.oreVeins.addAll(oreVeins);
        }

        public void addUndergroundFluids(List<UndergroundFluidPosition> undergroundFluids) {
            this.undergroundFluids.addAll(undergroundFluids);
        }

        private boolean sendData() {
            if(isEmpty()) return true;
            final ProspectionSyncMsg packet = new ProspectionSyncMsg();

            final int oresConsumed = packet.addOreVeins(oreVeins);
            oreVeins.subList(0, oresConsumed)
                .clear();

            final int fluidsConsumed = packet.addUndergroundFluids(undergroundFluids);
            undergroundFluids.subList(0, fluidsConsumed)
                .clear();

            SPNetwork.sendToTeamMembers(packet, team);

            return isEmpty();
        }

        private boolean isEmpty() {
            return oreVeins.isEmpty() && undergroundFluids.isEmpty();
        }
    }
}

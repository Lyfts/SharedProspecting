package com.rune580.sharedprospecting.database;

import com.rune580.sharedprospecting.SharedProspectingMod;
import com.rune580.sharedprospecting.Tags;
import com.rune580.sharedprospecting.utils.FsUtils;
import com.sinthoras.visualprospecting.database.DimensionCache;
import com.sinthoras.visualprospecting.database.OreVeinPosition;
import com.sinthoras.visualprospecting.database.UndergroundFluidPosition;
import com.sinthoras.visualprospecting.database.WorldCache;
import serverutils.lib.data.ForgeTeam;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TeamCache extends WorldCache {

    private final ForgeTeam team;
    private final String uuid;

    public TeamCache(ForgeTeam team) {
        this.team = team;
        this.uuid = team.getUIDCode();
    }

    protected File getStorageDirectory() {
        final File teamDir = com.sinthoras.visualprospecting.Utils.getSubDirectory(Tags.TEAMS_DIR);
        return new File(teamDir, uuid);
    }

    /**
     * Resets cache and deletes file from disk.
     */
    public void delete(String worldId) {
        reset();

        final File worldCacheDirectory = new File(getStorageDirectory(), worldId);
        if (!FsUtils.recursiveDelete(worldCacheDirectory))
            SharedProspectingMod.LOG.error("Failed to delete team cache for {}! File `{}` still exists on disk!", uuid, worldCacheDirectory);
    }

    public ForgeTeam getTeam() {
        return this.team;
    }

    public void addOreVeins(List<OreVeinPosition> oreVeins) {
        for (OreVeinPosition oreVein : oreVeins) {
            putOreVein(oreVein);
        }
    }

    public void addUndergroundFluids(List<UndergroundFluidPosition> undergroundFluidPositions) {
        for (UndergroundFluidPosition fluidPosition : undergroundFluidPositions) {
            putUndergroundFluids(fluidPosition);
        }
    }

    public List<OreVeinPosition> getAllOreVeins() {
        List<OreVeinPosition> allOreVeins = new ArrayList<>();
        for (DimensionCache dimension : dimensions.values()) {
            allOreVeins.addAll(dimension.getAllOreVeins());
        }
        return allOreVeins;
    }

    public List<UndergroundFluidPosition> getAllUndergroundFluids() {
        List<UndergroundFluidPosition> allUndergroundFluids = new ArrayList<>();
        for (DimensionCache dimension : dimensions.values()) {
            allUndergroundFluids.addAll(dimension.getAllUndergroundFluids());
        }
        return allUndergroundFluids;
    }
}
